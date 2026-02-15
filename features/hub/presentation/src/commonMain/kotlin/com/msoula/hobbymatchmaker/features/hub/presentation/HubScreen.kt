package com.msoula.hobbymatchmaker.features.hub.presentation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.organisms.HubFavoriteMoviesSection
import com.msoula.hobbymatchmaker.core.design.organisms.HubNoFavoriteMoviesSection
import com.msoula.hobbymatchmaker.core.design.templates.HubLayout
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.features.hub.presentation.mappers.toMovieCarouselItem
import com.msoula.hobbymatchmaker.features.hub.presentation.models.HubFavoriteMoviesUIModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun HubContent(
    hubFavoriteMovies: UiState<ImmutableList<HubFavoriteMoviesUIModel>>,
    observeFavoriteMovies: () -> Unit,
    navigateToMoviesScreen: () -> Unit,
    navigateToMovieDetail: (movieId: Long) -> Unit
) {
    Scaffold { paddingValues ->
        HubLayout(
            hubRecentMatches = {

            },
            hubFavoriteMovies = {
                StateContainer(
                    state = hubFavoriteMovies,
                    onEmpty = {
                        HubNoFavoriteMoviesSection(navigateToMoviesScreen)
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
                            navigateToMovieDetail = navigateToMovieDetail,
                        )
                    },
                )
            }
        )
    }
}
