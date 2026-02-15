package com.msoula.hobbymatchmaker.features.hub.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.features.hub.presentation.interactors.FavoriteMoviesSuccess
import com.msoula.hobbymatchmaker.features.hub.presentation.interactors.HubInteractor
import com.msoula.hobbymatchmaker.features.hub.presentation.models.HubFavoriteMoviesUIModel
import com.msoula.hobbymatchmaker.features.hub.presentation.models.HubRecentMatchesUIModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HubViewModel(
    private val hubInteractor: HubInteractor,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    private val scope = externalScope ?: viewModelScope

    private val eventHandler = EventHandler()
    val events = eventHandler.events

    val hubFavoriteMoviesState: StateFlow<UiState<List<HubFavoriteMoviesUIModel>>>
        field = MutableStateFlow<UiState<List<HubFavoriteMoviesUIModel>>>(UiState.Loading)

    val hubRecentMatchesState: StateFlow<UiState<List<HubRecentMatchesUIModel>>>
        field = MutableStateFlow<UiState<List<HubRecentMatchesUIModel>>>(UiState.Loading)

    init {
        observeRecentMatches()
        observeFavoriteMovies()
    }

    private fun observeRecentMatches() {

    }

    private fun observeFavoriteMovies() {
        scope.launch {
            hubInteractor.observeFavoriteMovies()
                .collect { result ->
                    when (val result = result) {
                        is AppResult.Success -> {
                            when (val data = result.data) {
                                is FavoriteMoviesSuccess.Empty ->
                                    hubFavoriteMoviesState.update { UiState.Empty }

                                is FavoriteMoviesSuccess.Success ->
                                    hubFavoriteMoviesState.update {
                                        UiState.Success(
                                            data.movies
                                        )
                                    }
                            }
                        }

                        is AppResult.Failure -> {
                            Logger.e("Error observing favorite movies: ${result.error}")
                            hubFavoriteMoviesState.update {
                                UiState.Error(
                                    defaultMessageMapper.toUIText(
                                        result.error
                                    )
                                )
                            }
                        }
                    }
                }
        }
    }
}
