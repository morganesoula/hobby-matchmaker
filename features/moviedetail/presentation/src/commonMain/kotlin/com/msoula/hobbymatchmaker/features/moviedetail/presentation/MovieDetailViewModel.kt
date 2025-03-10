package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieSuccess
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailViewStateModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.toMovieDetailUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val observeMovieDetailUseCase: ObserveMovieDetailUseCase,
    private val manageMovieTrailerUseCase: ManageMovieTrailerUseCase
) : ScreenModel {

    private val _oneTimeEventChannel = Channel<MovieDetailUiEventModel>()
    val oneTimeEventChannelFlow = _oneTimeEventChannel.receiveAsFlow()

    private val movieIdFlow = MutableStateFlow<Long?>(null)

    private var currentMovie: MovieDetailUiModel? = MovieDetailUiModel()
    private val language = getDeviceLocale()

    val viewState: StateFlow<MovieDetailViewStateModel> =
        movieIdFlow.filterNotNull().flatMapLatest { movieId ->
            observeMovieDetailUseCase(Parameters.LongStringParam(movieId, language))
                .map { result ->
                    when (result) {
                        is Result.Loading -> MovieDetailViewStateModel.Loading
                        is Result.Success -> {
                            when (val data = result.data) {
                                is ObserveMovieSuccess.Success -> {
                                    currentMovie = data.data.toMovieDetailUiModel()
                                    MovieDetailViewStateModel.Success(data.data.toMovieDetailUiModel())
                                }

                                else -> MovieDetailViewStateModel.Empty
                            }
                        }

                        is Result.Failure -> {
                            MovieDetailViewStateModel.Error(result.error.message)
                        }
                    }
                }
        }
            .stateIn(
                screenModelScope,
                SharingStarted.WhileSubscribed(5000),
                MovieDetailViewStateModel.Loading
            )

    fun loadMovieDetails(movieId: Long) = setMovieId(movieId)

    fun onEvent(event: MovieDetailUiEventModel) {
        when (event) {
            is MovieDetailUiEventModel.OnPlayMovieTrailerClicked -> {
                screenModelScope.launch(ioDispatcher) {
                    onPlayTrailerClicked(
                        event.movieId,
                        event.isVideoURIknown
                    )
                }
            }

            else -> Unit
        }
    }

    private suspend fun onPlayTrailerClicked(movieId: Long, isVideoURIknown: Boolean) {
        if (isVideoURIknown) {
            sendOnce(
                MovieDetailUiEventModel.OnPlayMovieTrailerReady(
                    currentMovie?.videoKey ?: ""
                )
            )
        } else {
            manageMovieTrailerUseCase(
                Parameters.LongStringParam(
                    movieId,
                    language
                )
            ).collect { result ->
                when (result) {
                    is Result.Success -> sendOnce(
                        MovieDetailUiEventModel.OnPlayMovieTrailerReady(
                            result.data.videoURI
                        )
                    )

                    is Result.Loading -> sendOnce(MovieDetailUiEventModel.LoadingTrailer)
                    is Result.Failure -> sendOnce(
                        MovieDetailUiEventModel.ErrorFetchingTrailer(
                            result.error.message
                        )
                    )
                }
            }
        }
    }

    private fun setMovieId(movieId: Long) {
        if (movieIdFlow.value != movieId) {
            movieIdFlow.value = movieId
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun sendOnce(event: MovieDetailUiEventModel) {
        if (!_oneTimeEventChannel.isClosedForSend) {
            _oneTimeEventChannel.send(event)
        }
    }
}
