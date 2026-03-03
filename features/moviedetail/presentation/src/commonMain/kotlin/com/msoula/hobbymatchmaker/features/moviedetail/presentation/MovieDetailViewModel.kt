package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
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
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.mappers.toMatchingMemberUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MatchingMemberUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.orchestrators.MovieDetailOrchestrator
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.orchestrators.MovieSuccess
import com.msoula.hobbymatchmaker.features.social.domain.models.MovieMatchResult
import com.msoula.hobbymatchmaker.features.social.domain.useCases.CheckMovieMatchUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.SyncFavoriteToCircleUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val movieId: Long,
    private val interactor: MovieDetailOrchestrator,
    private val syncFavoriteToCircleUseCase: SyncFavoriteToCircleUseCase,
    private val checkMovieMatchUseCase: CheckMovieMatchUseCase,
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
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { result ->
                when (result) {
                    is AppResult.Success -> {
                        when (val data = result.data) {
                            is MovieSuccess.Success -> {
                                movieDetailState.update { UiState.Success(data.data) }
                                updateSharedMembers()
                            }
                        }
                    }

                    is AppResult.Failure -> movieDetailState.update { mapError(result.error) }
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

        interactor.toggleFavorite(movieId, newFavoriteState)
            .onSuccess { uid ->
                scope.launch {
                    syncFavoriteToCircleUseCase(uid, movieId, newFavoriteState)
                }

                if (newFavoriteState) {
                    checkMovieMatchUseCase(uid, movieId)
                        .onSuccess { result ->
                            notifyMatchIfNeeded(result)
                            applySharedMembers(result)
                        }
                } else {
                    applySharedMembers(MovieMatchResult.NoMatch)
                }
            }
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }

    private suspend fun notifyMatchIfNeeded(result: MovieMatchResult) {
        if (result is MovieMatchResult.Match) {
            eventHandler.sendEvent(
                UiEvent.ShowSnackBar(
                    UIText.Resource(
                        Res.string.social_movie_match_notification,
                        listOf(result.matchingMemberDomainModels)
                    )
                )
            )
        }
    }

    private fun mapError(error: AppError) =
        UiState.Error(
            error = defaultMessageMapper.toUIText(error),
            hint = UIErrorHint(retry = RetryPolicy.Manual)
        )

    private fun updateSharedMembers() {
        scope.launch {
            val uid = interactor.getAuthenticatedUid() ?: return@launch
            checkMovieMatchUseCase(uid, movieId)
                .onSuccess { result -> applySharedMembers(result) }
        }
    }

    private fun applySharedMembers(result: MovieMatchResult) {
        movieDetailState.update { currentState ->
            if (currentState !is UiState.Success) return@update currentState
            val (members, isShared) = when (result) {
                is MovieMatchResult.Match -> result.matchingMemberDomainModels.map {
                    it.toMatchingMemberUiModel()
                } to result.matchingMemberDomainModels.isNotEmpty()

                else -> emptyList<MatchingMemberUiModel>() to false
            }

            UiState.Success(
                currentState.data.copy(sharedMembers = members, isShared = isShared)
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }
}
