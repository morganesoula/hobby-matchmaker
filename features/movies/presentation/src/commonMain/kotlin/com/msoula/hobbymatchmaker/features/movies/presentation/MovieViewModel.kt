package com.msoula.hobbymatchmaker.features.movies.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesErrors
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.toMovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModel(
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    observeAllMoviesUseCase: ObserveAllMoviesUseCase,
    private val getUserInfo: FetchFirebaseUserInfo,
    private val ioDispatcher: CoroutineDispatcher
) : ScreenModel {

    private val _oneTimeEventChannel = Channel<MovieUiEventModel>()
    val oneTimeEventChannelFlow = _oneTimeEventChannel.receiveAsFlow()

    private val language = getDeviceLocale()

    val movieState: StateFlow<MovieUiStateModel> =
        observeAllMoviesUseCase(Parameters.StringParam(language)).mapLatest { result ->
            when (result) {
                is Result.Success -> {
                    when (val movies = result.data) {
                        is ObserveAllMoviesSuccess.Loading -> MovieUiStateModel.Loading
                        is ObserveAllMoviesSuccess.Success ->
                            MovieUiStateModel.Success(movies.movies.map { it.toMovieUiModel() })

                        is ObserveAllMoviesSuccess.DataLoadedInDB -> MovieUiStateModel.Empty
                    }
                }

                is Result.Failure -> {
                    val errorMessage = handleError(result.error)
                    MovieUiStateModel.Error(errorMessage)
                }

                else -> MovieUiStateModel.Loading
            }
        }
            .stateIn(
                screenModelScope,
                SharingStarted.WhileSubscribed(5000),
                MovieUiStateModel.Loading
            )

    private suspend fun handleError(error: AppError): String {
        return when (error) {
            is ObserveAllMoviesErrors.NetworkError -> getString(Res.string.movies_network_error)
            is ObserveAllMoviesErrors.ApiError -> getString(Res.string.movies_api_error)
            is ObserveAllMoviesErrors.UnknownError -> getString(Res.string.movies_unknown_error)
            else -> error.message
        }
    }

    fun onCardEvent(event: CardEventModel) {
        when (event) {
            is CardEventModel.OnDoubleTap -> {
                toggleFavorite(event.movie.id, !event.movie.isFavorite)
            }

            is CardEventModel.OnSingleTap -> {
                screenModelScope.launch {
                    sendOnce(MovieUiEventModel.OnMovieDetailClicked(event.movieId))
                }
            }
        }
    }

    private fun toggleFavorite(movieId: Long, isFavorite: Boolean) {
        screenModelScope.launch(ioDispatcher) {
            val uuid = getUserInfo()?.uid
            setMovieFavoriteUseCase(uuid ?: "", movieId, isFavorite)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun sendOnce(event: MovieUiEventModel) {
        if (!_oneTimeEventChannel.isClosedForSend) {
            _oneTimeEventChannel.send(event)
        }
    }
}
