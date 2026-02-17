package com.msoula.hobbymatchmaker.features.hub.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.organisms.HubFavoriteMoviesSection
import com.msoula.hobbymatchmaker.core.design.organisms.HubNoFavoriteMoviesSection
import com.msoula.hobbymatchmaker.core.design.organisms.HubNoRecentMatchesSection
import com.msoula.hobbymatchmaker.core.design.organisms.HubRecentMatches
import com.msoula.hobbymatchmaker.core.design.templates.HubLayout
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.features.hub.presentation.mappers.toMovieCarouselItem
import com.msoula.hobbymatchmaker.features.hub.presentation.mappers.toProfileSocialMember
import com.msoula.hobbymatchmaker.features.hub.presentation.models.HubFavoriteMoviesUIModel
import com.msoula.hobbymatchmaker.features.hub.presentation.models.HubRecentMatchesUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun HubContent(
    hubFavoriteMovies: UiState<ImmutableList<HubFavoriteMoviesUIModel>>,
    hubRecentMatches: UiState<ImmutableList<HubRecentMatchesUIModel>>,
    observeFavoriteMovies: () -> Unit,
    observeRecentMatches: () -> Unit,
    navigateToMoviesScreen: () -> Unit,
    navigateToMovieDetail: (movieId: Long) -> Unit,
    navigateToProfileScreen: () -> Unit
) {
    Scaffold { paddingValues ->
        HubLayout(
            paddingValues = paddingValues,
            hubRecentMatches = {
                StateContainer(
                    state = hubRecentMatches,
                    onEmpty = {
                        HubNoRecentMatchesSection(navigateToProfileScreen = navigateToProfileScreen)
                    },
                    onError = { error, hint ->
                        ErrorStateScreen(
                            error = error,
                            hint = hint,
                            onRetry = { observeRecentMatches() })
                    },
                    onSuccess = { members ->
                        HubRecentMatches(
                            members = members.map { member -> member.toProfileSocialMember() }
                                .toImmutableList()
                        )
                    }
                )
            },
            hubFavoriteMovies = {
                StateContainer(
                    state = hubFavoriteMovies,
                    onEmpty = {
                        HubNoFavoriteMoviesSection(navigateToMoviesScreen = navigateToMoviesScreen)
                    },
                    onError = { error, hint ->
                        ErrorStateScreen(
                            error = error,
                            hint = hint,
                            onRetry = { observeFavoriteMovies() }
                        )
                    },
                    onSuccess = { movies ->
                        HubFavoriteMoviesSection(
                            likedMovies = movies.map { movie -> movie.toMovieCarouselItem() }
                                .toImmutableList(),
                            navigateToMovieDetail = navigateToMovieDetail
                        )
                    },
                )
            }
        )
    }
}
