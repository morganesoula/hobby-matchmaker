package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.onSuccess
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.social_movie_match_notification
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.RetryPolicy
import com.msoula.hobbymatchmaker.core.design.util.UIErrorHint
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.interactors.MovieDetailInteractor
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.interactors.MovieMatchUiSuccess
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.interactors.MovieSuccess
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieId: Long,
    private val interactor: MovieDetailInteractor,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    private val scope = externalScope ?: viewModelScope
    private val eventHandler = EventHandler()
    val events = eventHandler.events

    val movieDetailState: StateFlow<UiState<MovieDetailUiModel>>
        field = MutableStateFlow<UiState<MovieDetailUiModel>>(UiState.Loading)

    private val retryTrigger = MutableStateFlow(0)

    private val language = getDeviceLocale()

    init {
        observeData()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeData() {
        retryTrigger
            .flatMapLatest { interactor.observeMovieDetail(movieId, language) }
            .onEach { result ->
                movieDetailState.update {
                    when (result) {
                        is AppResult.Success -> mapDetailSuccess(result.data)
                        is AppResult.Failure -> mapError(result.error)
                    }
                }
            }
            .launchIn(scope)
    }

    fun retryObservation() {
        movieDetailState.update { UiState.Loading }
        retryTrigger.update { it + 1 }
    }

    fun onEvent(event: MovieDetailUiEventModel) {
        when (event) {
            is MovieDetailUiEventModel.OnPlayMovieTrailerClicked ->
                scope.launch { playTrailer(event.isVideoURIknown) }

            is MovieDetailUiEventModel.OnMovieDoubleTap ->
                scope.launch { toggleFavorite(event.movieId) }
        }
    }

    private suspend fun playTrailer(isVideoUriKnown: Boolean) {
        if (isVideoUriKnown) {
            val currentState = movieDetailState.value
            if (currentState is UiState.Success) {
                eventHandler.sendEvent(UiEvent.OnDataReady(currentState.data.videoKey))
            }
            return
        }

        interactor.fetchTrailer(movieId, language)
            .onSuccess { eventHandler.sendEvent(UiEvent.OnDataReady(it)) }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(
                        defaultMessageMapper.toUIText(error)
                    )
                )
            }
    }

    private suspend fun toggleFavorite(movieId: Long) {
        val currentState = movieDetailState.value
        if (currentState !is UiState.Success) return

        val movie = currentState.data
        val newFavoriteState = !movie.isFavorite

        val uid = interactor.getAuthenticatedUid()

        interactor.toggleFavorite(movieId, newFavoriteState)
            .onSuccess {
                if (newFavoriteState && uid != null) {
                    checkMatchAndNotify(uid, movieId)
                }
                fetchSharedMembers(movie)
            }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }

    private suspend fun checkMatchAndNotify(uid: String, movieId: Long) {
        interactor.checkForMovieMatch(uid, movieId)
            .onSuccess { result ->
                if (result is MovieMatchUiSuccess) {
                    eventHandler.sendEvent(
                        UiEvent.ShowSnackBar(
                            UIText.Resource(
                                Res.string.social_movie_match_notification,
                                listOf(result.matchingMembers)
                            )
                        )
                    )
                }
            }
            .onFailure { error ->
                Logger.e("Failed to check movie match: $error")
            }
    }

    private fun mapDetailSuccess(success: MovieSuccess): UiState<MovieDetailUiModel> =
        when (success) {
            is MovieSuccess.Success -> {
                val uiModel = success.data
                fetchSharedMembers(uiModel)
                UiState.Success(uiModel)
            }

            is MovieSuccess.Loaded -> UiState.Loading
        }

    private fun mapError(error: AppError) =
        UiState.Error(
            error = defaultMessageMapper.toUIText(error),
            hint = UIErrorHint(retry = RetryPolicy.Manual)
        )

    private fun fetchSharedMembers(movie: MovieDetailUiModel) {
        scope.launch {
            val uid = interactor.getAuthenticatedUid() ?: return@launch
            interactor.checkForMovieMatch(uid, movie.id)
                .onSuccess { result ->
                    if (result is MovieMatchUiSuccess) {
                        movieDetailState.update { currentState ->
                            if (currentState is UiState.Success) {
                                UiState.Success(
                                    currentState.data.copy(
                                        sharedMembers = result.matchingMembers,
                                        isShared = result.matchingMembers.isNotEmpty()
                                    )
                                )
                            } else currentState
                        }
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }
}
