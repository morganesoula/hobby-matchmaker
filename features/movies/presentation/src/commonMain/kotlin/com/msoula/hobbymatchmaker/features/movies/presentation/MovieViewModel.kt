package com.msoula.hobbymatchmaker.features.movies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.CheckMovieSynopsisValueUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModel(
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    private val observeAllMoviesUseCase: ObserveAllMoviesUseCase,
    private val getUserInfo: FetchFirebaseUserInfo,
    private val logOutUseCase: LogOutUseCase,
    private val checkMovieSynopsisValueUseCase: CheckMovieSynopsisValueUseCase,
    private val connectivityCheck: NetworkConnectivityChecker,
    private val ioDispatcher: CoroutineDispatcher,
    private val errorMessageProvider: ErrorMessageProvider
) : ViewModel() {

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
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                MovieUiStateModel.Loading
            )

    fun logOut() {
        viewModelScope.launch {
            logOutUseCase(Parameters.StringParam("")).collect { result ->
                when (result) {
                    is Result.Success -> _oneTimeEventChannel.trySend(MovieUiEventModel.OnLogOutSuccess)
                    is Result.Failure -> _oneTimeEventChannel.trySend(
                        MovieUiEventModel.OnLogOutFailure(
                            result.error.message
                        )
                    )

                    else -> Unit
                }
            }
        }
    }

    private suspend fun handleError(error: AppError): String =
        errorMessageProvider.getMessage(error)

    fun onCardEvent(event: CardEventModel) {
        when (event) {
            is CardEventModel.OnDoubleTap -> {
                toggleFavorite(event.movie.id, !event.movie.isFavorite)
            }

            is CardEventModel.OnSingleTap -> {
                viewModelScope.launch {
                    sendOnce(handleSingleTap(event.movieId))
                }
            }
        }
    }

    internal suspend fun handleSingleTap(movieId: Long): MovieUiEventModel {
        val localData = checkMovieSynopsisValueUseCase(movieId)
        val hasConnectivity = connectivityCheck.hasActiveConnection()

        return when {
            localData -> MovieUiEventModel.OnMovieDetailClicked(movieId)
            hasConnectivity -> MovieUiEventModel.OnMovieDetailClicked(movieId)
            else -> MovieUiEventModel.NoFetchingDetailPossible
        }
    }

    private fun toggleFavorite(movieId: Long, isFavorite: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            val uuid = getUserInfo()?.uid
            setMovieFavoriteUseCase(uuid ?: "", movieId, isFavorite)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendOnce(event: MovieUiEventModel) {
        if (!_oneTimeEventChannel.isClosedForSend) {
            _oneTimeEventChannel.trySend(event)
        }
    }
}
