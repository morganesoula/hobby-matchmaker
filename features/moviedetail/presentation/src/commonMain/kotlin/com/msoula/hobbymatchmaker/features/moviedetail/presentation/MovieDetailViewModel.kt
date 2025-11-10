package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.connection_issue
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.RetryPolicy
import com.msoula.hobbymatchmaker.core.design.util.UIErrorHint
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieSuccess
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.toMovieDetailUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieId: Long,
    private val observeMovieDetailUseCase: ObserveMovieDetailUseCase,
    private val manageMovieTrailerUseCase: ManageMovieTrailerUseCase,
    private val connectivityCheck: NetworkConnectivityChecker,
    private val defaultErrorMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    val scope = externalScope ?: viewModelScope

    private val _events: Channel<UiEvent> = Channel(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _screenState = MutableStateFlow<UiState<MovieDetailUiModel>>(UiState.Loading)
    val screenState = _screenState.asStateFlow()

    private var currentMovie: MovieDetailUiModel? = MovieDetailUiModel()
    private val language = getDeviceLocale()

    init {
        observeMovieDetail()
    }

    fun observeMovieDetail() {
        scope.launch {
            observeMovieDetailUseCase(movieId, language)
                .onStart {
                    Logger.d("Inside MovieDetailVM with movieId: $movieId")
                }
                .collect { result ->
                    _screenState.update {
                        when (result) {
                            is AppResult.Success -> {
                                when (val payload = result.data) {
                                    is ObserveMovieSuccess.Success -> {
                                        currentMovie = payload.data.toMovieDetailUiModel()
                                        UiState.Success(requireNotNull(currentMovie))
                                    }

                                    is ObserveMovieSuccess.DataLoadedInDB -> UiState.Loading
                                }
                            }

                            is AppResult.Failure -> UiState.Error(
                                error = defaultErrorMessageMapper.toUIText(result.error),
                                hint = UIErrorHint(retry = RetryPolicy.Manual)
                            )
                        }
                    }

                }
        }
    }

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
            if (connectivityCheck.hasActiveConnection()) {
                sendEvent(UiEvent.OnDataReady(currentMovie?.videoKey.orEmpty()))
            } else {
                sendEvent(UiEvent.ShowSnackBar(UIText.Resource(Res.string.connection_issue)))
            }
            return
        }

        manageMovieTrailerUseCase(movieId, language)
            .onFailure { error ->
                sendEvent(UiEvent.ShowSnackBar(defaultErrorMessageMapper.toUIText(error)))
            }
            .onSuccess { data ->
                sendEvent(UiEvent.OnDataReady(data.videoURI))
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendEvent(event: UiEvent) {
        if (!_events.isClosedForSend) {
            _events.trySend(event)
        }
    }
}
