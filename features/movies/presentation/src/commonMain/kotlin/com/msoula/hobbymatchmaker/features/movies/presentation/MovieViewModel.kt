package com.msoula.hobbymatchmaker.features.movies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.getDeviceLocale
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.connection_issue
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.RetryPolicy
import com.msoula.hobbymatchmaker.core.design.util.UIErrorHint
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesSuccess
import com.msoula.hobbymatchmaker.features.movies.presentation.interactors.MovieInteractor
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.toMovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModel(
    private val interactor: MovieInteractor,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {

    private val scope = externalScope ?: viewModelScope
    private val eventHandler = EventHandler()
    val events = eventHandler.events

    private val language = getDeviceLocale()

    private val _screenState = MutableStateFlow<UiState<List<MovieUiModel>>>(UiState.Loading)
    val screenState = _screenState.asStateFlow()

    init {
        observeMovies()
    }

    fun observeMovies() {
        scope.launch {
            interactor.observeMovies((language)).collect { result ->
                _screenState.update {
                    when (result) {
                        is AppResult.Success -> mapSuccess(result.data)
                        is AppResult.Failure -> mapError(result.error)
                    }
                }
            }
        }
    }

    fun onCardEvent(event: CardEventModel) {
        when (event) {
            is CardEventModel.OnDoubleTap -> scope.launch { toggleFavorite(event.movie.id) }
            is CardEventModel.OnSingleTap -> scope.launch { handleSingleTap(event.movieId) }
        }
    }

    internal suspend fun handleSingleTap(movieId: Long) {
        val canAccess = interactor.canAccessMovieDetail(movieId)

        if (canAccess) {
            eventHandler.sendEvent(UiEvent.NavigateToDetail(movieId))
        } else {
            eventHandler.sendEvent(
                UiEvent.ShowSnackBar(UIText.Resource(Res.string.connection_issue))
            )
        }
    }

    private suspend fun toggleFavorite(movieId: Long) {
        val currentState = _screenState.value
        if (currentState !is UiState.Success) return

        val movie = currentState.data.firstOrNull { it.id == movieId } ?: return
        val newFavoriteState = !movie.isFavorite

        interactor.toggleFavorite(movieId, newFavoriteState)
            .onFailure { error ->
                eventHandler.sendEvent(
                    UiEvent.ShowSnackBar(defaultMessageMapper.toUIText(error))
                )
            }
    }

    private fun mapSuccess(success: ObserveAllMoviesSuccess): UiState<List<MovieUiModel>> =
        when (success) {
            is ObserveAllMoviesSuccess.Success -> {
                val movies = success.movies.map { it.toMovieUiModel() }
                if (movies.isEmpty()) UiState.Empty else UiState.Success(movies)
            }

            is ObserveAllMoviesSuccess.DataLoadedInDB -> UiState.Loading
        }

    private fun mapError(error: AppError): UiState.Error =
        UiState.Error(
            defaultMessageMapper.toUIText(error),
            hint = UIErrorHint(retry = RetryPolicy.Manual)
        )

    override fun onCleared() {
        super.onCleared()
        eventHandler.close()
    }
}
