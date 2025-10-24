package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.common.route
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieSuccess
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailViewStateModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.toMovieDetailUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    movieId: Long,
    observeMovieDetailUseCase: ObserveMovieDetailUseCase,
    private val manageMovieTrailerUseCase: ManageMovieTrailerUseCase,
    private val connectivityCheck: NetworkConnectivityChecker,
    private val defaultErrorMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    val scope = externalScope ?: viewModelScope
    private val _oneTimeEventChannel = Channel<MovieDetailUiEventModel>()
    val oneTimeEventChannelFlow = _oneTimeEventChannel.receiveAsFlow()
    private var currentMovie: MovieDetailUiModel? = MovieDetailUiModel()
    private val language = getDeviceLocale()

    val viewState: StateFlow<MovieDetailViewStateModel> =
        observeMovieDetailUseCase(movieId, language)
            .onStart {
                Logger.d("Inside MovieDetailVM with movieId: $movieId")
            }
            .map { result ->
                when (result) {
                    is AppResult.Success -> {
                        when (val success = result.data) {
                            is ObserveMovieSuccess.Success -> {
                                currentMovie = success.data.toMovieDetailUiModel()
                                MovieDetailViewStateModel.Success(requireNotNull(currentMovie))
                            }

                            is ObserveMovieSuccess.DataLoadedInDB -> MovieDetailViewStateModel.Loading
                        }
                    }

                    is AppResult.Failure -> {
                        val event = result.error.route(
                            onConnectivity = {
                                MovieDetailViewStateModel.Error(
                                    defaultErrorMessageMapper.toUIText(result.error)
                                )
                            },
                            onUserActionRequired = {
                                MovieDetailViewStateModel.Error(
                                    defaultErrorMessageMapper.toUIText(result.error)
                                )
                            },
                            onOther = {
                                MovieDetailViewStateModel.Error(
                                    defaultErrorMessageMapper.toUIText(result.error)
                                )
                            }
                        )
                        event
                    }
                }
            }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = MovieDetailViewStateModel.Loading
            )

    fun onEvent(event: MovieDetailUiEventModel) {
        when (event) {
            is MovieDetailUiEventModel.OnPlayMovieTrailerClicked -> {
                scope.launch {
                    onPlayTrailerClicked(
                        event.movieId,
                        event.isVideoURIknown
                    )
                }
            }

            else -> Unit
        }
    }

    @VisibleForTesting
    internal suspend fun onPlayTrailerClicked(movieId: Long, isVideoURIknown: Boolean) {
        if (isVideoURIknown) {
            sendOnce(
                if (connectivityCheck.hasActiveConnection())
                    MovieDetailUiEventModel.OnPlayMovieTrailerReady(
                        currentMovie?.videoKey.orEmpty()
                    )
                else MovieDetailUiEventModel.NoConnection
            )
            return
        }

        sendOnce(MovieDetailUiEventModel.LoadingTrailer)

        manageMovieTrailerUseCase(movieId, language)
            .onFailure {
                sendOnce(
                    it.route(
                        onConnectivity = { MovieDetailUiEventModel.NoConnection },
                        onUserActionRequired = { MovieDetailUiEventModel.ErrorFetchingTrailer },
                        onOther = { MovieDetailUiEventModel.ErrorFetchingTrailer }
                    )
                )
            }
            .onSuccess {
                sendOnce(
                    MovieDetailUiEventModel.OnPlayMovieTrailerReady(
                        it.videoURI
                    )
                )
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun sendOnce(event: MovieDetailUiEventModel) {
        if (!_oneTimeEventChannel.isClosedForSend) {
            _oneTimeEventChannel.send(event)
        }
    }
}
