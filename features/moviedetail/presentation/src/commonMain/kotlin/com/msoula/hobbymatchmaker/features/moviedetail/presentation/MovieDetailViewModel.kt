package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.FetchingTrailerError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieSuccess
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailViewStateModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.toMovieDetailUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    movieId: Long,
    private val ioDispatcher: CoroutineDispatcher,
    observeMovieDetailUseCase: ObserveMovieDetailUseCase,
    private val manageMovieTrailerUseCase: ManageMovieTrailerUseCase,
    private val connectivityCheck: NetworkConnectivityChecker
) : ViewModel() {

    private val _oneTimeEventChannel = Channel<MovieDetailUiEventModel>()
    val oneTimeEventChannelFlow = _oneTimeEventChannel.receiveAsFlow()

    private var currentMovie: MovieDetailUiModel? = MovieDetailUiModel()
    private val language = getDeviceLocale()

    val viewState: StateFlow<MovieDetailViewStateModel> =
        observeMovieDetailUseCase(Parameters.LongStringParam(movieId, language))
            .map { result ->
                when (result) {
                    is Result.Loading -> MovieDetailViewStateModel.Loading
                    is Result.Success -> {
                        when (val data = result.data) {
                            is ObserveMovieSuccess.Success -> {
                                currentMovie = data.data.toMovieDetailUiModel()
                                MovieDetailViewStateModel.Success(currentMovie!!)
                            }

                            else -> MovieDetailViewStateModel.Empty
                        }
                    }

                    is Result.Failure -> MovieDetailViewStateModel.Error(result.error.message)
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                MovieDetailViewStateModel.Loading
            )

    fun onEvent(event: MovieDetailUiEventModel) {
        when (event) {
            is MovieDetailUiEventModel.OnPlayMovieTrailerClicked -> {
                viewModelScope.launch(ioDispatcher) {
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
                if (connectivityCheck.hasActiveConnection()) {
                    MovieDetailUiEventModel.OnPlayMovieTrailerReady(
                        currentMovie?.videoKey ?: ""
                    )
                } else {
                    Logger.e("No connection from the VM point of view")
                    MovieDetailUiEventModel.NoConnection
                }
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
                    is Result.Failure -> {
                        sendOnce(
                            when (result.error) {
                                is FetchingTrailerError.NoConnectionError -> MovieDetailUiEventModel.NoConnection
                                else -> {
                                    Logger.e("Error fetching trailer - ${result.error.message}")
                                    MovieDetailUiEventModel.ErrorFetchingTrailer
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun sendOnce(event: MovieDetailUiEventModel) {
        if (!_oneTimeEventChannel.isClosedForSend) {
            _oneTimeEventChannel.send(event)
        }
    }
}
