package com.msoula.hobbymatchmaker.features.hub.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.models.MovieCarouselItem
import com.msoula.hobbymatchmaker.core.design.models.ProfileSocialMember
import com.msoula.hobbymatchmaker.core.design.organisms.HubFavoriteMoviesSection
import com.msoula.hobbymatchmaker.core.design.organisms.HubNoFavoriteMoviesSection
import com.msoula.hobbymatchmaker.core.design.organisms.HubNoRecentMatchesSection
import com.msoula.hobbymatchmaker.core.design.organisms.HubRecentMatches
import com.msoula.hobbymatchmaker.core.design.templates.HubLayout
import com.msoula.hobbymatchmaker.core.design.util.UiState
import kotlinx.collections.immutable.ImmutableList

@Composable
fun HubContent(
    hubFavoriteMovies: UiState<ImmutableList<MovieCarouselItem>>,
    hubRecentMatches: UiState<ImmutableList<ProfileSocialMember>>,
    observeFavoriteMovies: () -> Unit,
    getRecentMatches: () -> Unit,
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
                            onRetry = { getRecentMatches() })
                    },
                    onSuccess = { members ->
                        HubRecentMatches(
                            members = members
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
                            likedMovies = movies,
                            navigateToMovieDetail = navigateToMovieDetail
                        )
                    }
                )
            }
        )
    }
}
