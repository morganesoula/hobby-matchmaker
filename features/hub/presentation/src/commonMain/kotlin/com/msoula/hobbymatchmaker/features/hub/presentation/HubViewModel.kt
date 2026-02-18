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
import com.msoula.hobbymatchmaker.features.hub.presentation.interactors.RecentMatchesSuccess
import com.msoula.hobbymatchmaker.features.hub.presentation.models.HubFavoriteMoviesUIModel
import com.msoula.hobbymatchmaker.features.hub.presentation.models.HubRecentMatchesUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
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

    val hubFavoriteMoviesState: StateFlow<UiState<ImmutableList<HubFavoriteMoviesUIModel>>>
        field = MutableStateFlow<UiState<ImmutableList<HubFavoriteMoviesUIModel>>>(UiState.Loading)

    val hubRecentMatchesState: StateFlow<UiState<ImmutableList<HubRecentMatchesUIModel>>>
        field = MutableStateFlow<UiState<ImmutableList<HubRecentMatchesUIModel>>>(UiState.Loading)

    init {
        observeRecentMatches()
        observeFavoriteMovies()
    }

    fun observeRecentMatches() {
        scope.launch {
            hubRecentMatchesState.update {
                when (val result = hubInteractor.getMatchedFriends()) {
                    is AppResult.Success -> {
                        when (val data = result.data) {
                            is RecentMatchesSuccess.Empty -> UiState.Empty
                            is RecentMatchesSuccess.Success -> UiState.Success(data.friends.toImmutableList())
                        }
                    }

                    is AppResult.Failure -> UiState.Error(
                        defaultMessageMapper.toUIText(
                            result.error
                        )
                    )
                }
            }
        }
    }

    fun observeFavoriteMovies() {
        scope.launch {
            hubInteractor.observeFavoriteMovies()
                .collect { result ->
                    hubFavoriteMoviesState.update {
                        when (val result = result) {
                            is AppResult.Success -> {
                                when (val data = result.data) {
                                    is FavoriteMoviesSuccess.Empty -> UiState.Empty

                                    is FavoriteMoviesSuccess.Success ->
                                        UiState.Success(
                                            data.movies.toImmutableList()
                                        )
                                }
                            }

                            is AppResult.Failure -> {
                                Logger.e("Error observing favorite movies: ${result.error}")
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
