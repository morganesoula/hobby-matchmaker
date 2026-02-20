package com.msoula.hobbymatchmaker.features.hub.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.design.models.MovieCarouselItem
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.design.util.EventHandler
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.features.hub.presentation.interactors.HubInteractor
import com.msoula.hobbymatchmaker.features.hub.presentation.mappers.toMovieCarouselItem
import com.msoula.hobbymatchmaker.features.hub.presentation.mappers.toProfileSocialMember
import com.msoula.hobbymatchmaker.features.hub.presentation.models.FavoriteMoviesSuccess
import com.msoula.hobbymatchmaker.features.hub.presentation.models.HubSection
import com.msoula.hobbymatchmaker.features.hub.presentation.models.RecentMatchesSuccess
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class HubViewModel(
    private val hubInteractor: HubInteractor,
    private val defaultMessageMapper: ErrorMessageMapper,
    externalScope: CoroutineScope? = null
) : ViewModel() {
    private val scope = externalScope ?: viewModelScope

    private val eventHandler = EventHandler()
    val events = eventHandler.events

    val hubFavoriteMoviesState: StateFlow<UiState<ImmutableList<MovieCarouselItem>>>
        field = MutableStateFlow<UiState<ImmutableList<MovieCarouselItem>>>(UiState.Loading)

    val hubRecentMatchesState: StateFlow<UiState<ImmutableList<ProfileSocialMember>>>
        field = MutableStateFlow<UiState<ImmutableList<ProfileSocialMember>>>(UiState.Loading)

    private val favoriteMoviesRetryTrigger = MutableStateFlow(0)
    private val recentMatchesRetryTrigger = MutableStateFlow(0)

    init {
        observeFavoriteMovies()
        observeRecentMatches()
    }

    private fun observeFavoriteMovies() {
        favoriteMoviesRetryTrigger
            .flatMapLatest { hubInteractor.observeFavoriteMovies() }
            .onEach { result ->
                hubFavoriteMoviesState.update {
                    when (result) {
                        is AppResult.Success -> {
                            when (val data = result.data) {
                                is FavoriteMoviesSuccess.Empty -> UiState.Empty

                                is FavoriteMoviesSuccess.Success ->
                                    UiState.Success(
                                        data.movies.map { movie ->
                                            movie.toMovieCarouselItem()
                                        }.toImmutableList()
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
            .launchIn(scope)
    }

    private fun observeRecentMatches() {
        recentMatchesRetryTrigger
            .flatMapLatest { hubInteractor.observeMatchedFriends() }
            .onEach { result ->
                hubRecentMatchesState.update {
                    when (result) {
                        is AppResult.Success -> {
                            when (val data = result.data) {
                                is RecentMatchesSuccess.Empty -> UiState.Empty
                                is RecentMatchesSuccess.Success ->
                                    UiState.Success(data.friends.map { member ->
                                        member.toProfileSocialMember()
                                    }.toImmutableList())
                            }
                        }

                        is AppResult.Failure -> {
                            Logger.e("Error observing recent matches: ${result.error}")
                            UiState.Error(
                                defaultMessageMapper.toUIText(
                                    result.error
                                )
                            )
                        }
                    }
                }
            }
            .launchIn(scope)
    }

    fun retryObservation(section: HubSection) {
        when (section) {
            HubSection.FAVORITE_MOVIES -> {
                hubFavoriteMoviesState.update { UiState.Loading }
                favoriteMoviesRetryTrigger.update { it + 1 }
            }

            HubSection.RECENT_MATCHES -> {
                hubRecentMatchesState.update { UiState.Loading }
                recentMatchesRetryTrigger.update { it + 1 }
            }
        }
    }
}
