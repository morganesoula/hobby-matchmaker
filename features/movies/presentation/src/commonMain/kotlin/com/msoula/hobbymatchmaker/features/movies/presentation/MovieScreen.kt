package com.msoula.hobbymatchmaker.features.movies.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MovieFilter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.EmptyStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.MovieListLoadingScreen
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.atoms.asStringSuspend
import com.msoula.hobbymatchmaker.core.design.models.EmptyStateConfig
import com.msoula.hobbymatchmaker.core.design.molecules.NavigationTopBar
import com.msoula.hobbymatchmaker.core.design.no_data
import com.msoula.hobbymatchmaker.core.design.not_found
import com.msoula.hobbymatchmaker.core.design.organisms.MovieCarousel
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.toCarouselItems
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MovieContent(
    modifier: Modifier = Modifier,
    movieViewModel: MovieViewModel,
    onNavigate: (String) -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val movieState by movieViewModel.screenState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(movieViewModel.events) {
        movieViewModel.events.collect { event ->
            when (event) {
                is UiEvent.NavigateToRoute -> onNavigate(event.route)
                is UiEvent.NavigateToDetail -> onNavigateToDetail(event.id)
                is UiEvent.ShowSnackBar ->
                    snackBarHostState.showSnackbar(event.message.asStringSuspend())

                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            NavigationTopBar(
                redirectToProfile = { onNavigate("profile") },
                redirectToSocial = { onNavigate("social") }
            )
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                Snackbar(
                    modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite }
                        .padding(horizontal = 16.dp).fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = data.visuals.message)
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            StateContainer(
                state = movieState,
                onLoading = {
                    MovieListLoadingScreen(5)
                },
                onEmpty = {
                    EmptyStateScreen(
                        config = EmptyStateConfig(
                            icon = Icons.Outlined.MovieFilter,
                            title = UIText.Resource(Res.string.no_data),
                            description = UIText.Resource(Res.string.not_found)
                        )
                    )
                },
                onError = { error, hint ->
                    ErrorStateScreen(
                        error = error,
                        hint = hint,
                        onRetry = { movieViewModel.observeMovies() }
                    )
                },
                onSuccess = { movies ->
                    MovieCarousel(
                        padding = padding,
                        movies = movies.toImmutableList().toCarouselItems(),
                        onMovieSingleTap = { id, overview ->
                            movieViewModel.onCardEvent(CardEventModel.OnSingleTap(id, overview))
                        },
                        onMovieDoubleTap = { id ->
                            val selectedMovie = movies.first { it.id == id }
                            movieViewModel.onCardEvent(CardEventModel.OnDoubleTap(selectedMovie))
                        }
                    )
                }
            )
        }
    }
}
