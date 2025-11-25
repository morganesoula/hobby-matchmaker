package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.RetryPolicy
import com.msoula.hobbymatchmaker.core.design.util.UIErrorHint
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieSuccess
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.interactors.MovieDetailInteractor
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.toMovieDetailUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieId: Long,
    private val interactor: MovieDetailInteractor,
    private val defaultErrorMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    val scope = externalScope ?: viewModelScope

    private val eventHandler = EventHandler()
    val events = eventHandler.events
    private val _screenState = MutableStateFlow<UiState<MovieDetailUiModel>>(UiState.Loading)
    val screenState = _screenState.asStateFlow()

    private val language = getDeviceLocale()
    private var currentMovie: MovieDetailUiModel? = MovieDetailUiModel()

    init {
        observeMovieDetail()
    }

    fun observeMovieDetail() {
        scope.launch {
            interactor.observeMovieDetail(movieId, language).collect { result ->
                _screenState.update {
                    when (result) {
                        is AppResult.Success -> mapDetailSuccess(result.data)
                        is AppResult.Failure -> mapError(result.error)
                    }
                }
            }
        }
    }

    fun onEvent(event: MovieDetailUiEventModel) {
        when (event) {
            is MovieDetailUiEventModel.OnPlayMovieTrailerClicked ->
                scope.launch { playTrailer(event.isVideoURIknown) }

            else -> Unit
        }
    }

    private suspend fun playTrailer(isVideoUriKnown: Boolean) {
        if (interactor.canPlayTrailerDirectly(isVideoUriKnown)) {
            eventHandler.sendEvent(UiEvent.OnDataReady(currentMovie?.videoKey.orEmpty()))
            return
        }

        interactor.fetchTrailer(movieId, language)
            .onSuccess { eventHandler.sendEvent(UiEvent.OnDataReady(it)) }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(
                        defaultErrorMessageMapper.toUIText(error)
                    )
                )
            }
    }

    private suspend fun mapDetailSuccess(success: ObserveMovieSuccess): UiState<MovieDetailUiModel> =
        when (success) {
            is ObserveMovieSuccess.Success -> {
                val uiModel = success.data.toMovieDetailUiModel()
                currentMovie = uiModel
                UiState.Success(uiModel)
            }

            is ObserveMovieSuccess.DataLoadedInDB -> UiState.Loading
        }

    private fun mapError(error: AppError) =
        UiState.Error(
            error = defaultErrorMessageMapper.toUIText(error),
            hint = UIErrorHint(retry = RetryPolicy.Manual)
        )

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }
}
