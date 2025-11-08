package com.msoula.hobbymatchmaker.features.movies.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.design.CallOnceEffect
import com.msoula.hobbymatchmaker.core.design.ObserveEvents
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.SnackEffect
import com.msoula.hobbymatchmaker.core.design.atoms.LoadingCircularProgress
import com.msoula.hobbymatchmaker.core.design.molecules.NavigationTopBar
import com.msoula.hobbymatchmaker.core.design.no_fetching_detail_possible
import com.msoula.hobbymatchmaker.core.design.organisms.MovieCarousel
import com.msoula.hobbymatchmaker.core.design.templates.MovieLayout
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.atoms.asString
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.toCarouselItems
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import kotlinx.coroutines.flow.Flow

@Composable
fun MovieContent(
    modifier: Modifier = Modifier,
    movieViewModel: MovieViewModel,
    movieState: MovieUiStateModel,
    oneTimeEventChannelFlow: Flow<MovieUiEventModel>,
    redirectToMovieDetail: (Long) -> Unit,
    redirectToAuth: () -> Unit,
    redirectToProfile: () -> Unit
) {
    when (movieState) {
        is MovieUiStateModel.Success -> {
            MovieScreenContent(
                modifier = modifier,
                movies = movieState.list,
                oneTimeEventChannelFlow = oneTimeEventChannelFlow,
                redirectToMovieDetail = redirectToMovieDetail,
                onCardEvent = movieViewModel::onCardEvent,
                logOut = { movieViewModel.logOut() },
                redirectToAuth = redirectToAuth,
                redirectToProfile = redirectToProfile
            )
        }

        is MovieUiStateModel.Empty -> EmptyMovieScreen()
        is MovieUiStateModel.Error -> ErrorMovieScreen(error = movieState.errorMessage.asString())
        else -> LoadingCircularProgress()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreenContent(
    modifier: Modifier = Modifier,
    movies: List<MovieUiModel>,
    oneTimeEventChannelFlow: Flow<MovieUiEventModel>,
    redirectToMovieDetail: (movieId: Long) -> Unit,
    logOut: () -> Unit,
    onCardEvent: (CardEventModel) -> Unit,
    redirectToAuth: () -> Unit,
    redirectToProfile: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }

    ObserveEvents(oneTimeEventChannelFlow) { event ->
        when (event) {
            is MovieUiEventModel.OnMovieDetailClicked ->
                CallOnceEffect(event) {
                    Logger.d("Redirecting to movie detail as asked")
                    redirectToMovieDetail(event.movieId)
                }

            is MovieUiEventModel.OnLogOutSuccess ->
                CallOnceEffect(event) {
                    redirectToAuth()
                }

            is MovieUiEventModel.OnMovieUiFetchedError ->
                SnackEffect(snackBarHostState, event.error, event)

            is MovieUiEventModel.OnLogOutFailure ->
                SnackEffect(snackBarHostState, event.error, event)

            is MovieUiEventModel.ShowError ->
                SnackEffect(snackBarHostState, event.error, event)

            is MovieUiEventModel.NoFetchingDetailPossible ->
                SnackEffect(
                    snackBarHostState,
                    UIText.Resource(Res.string.no_fetching_detail_possible),
                    event
                )
        }
    }

    MovieLayout(
        snackBarHost = {
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
        },
        topBar = {
            NavigationTopBar(
                redirectToProfile = redirectToProfile,
                redirectToSignIn = logOut
            )
        },
        movieSection = { paddingValues ->
            MovieCarousel(
                padding = paddingValues,
                movies = movies.toCarouselItems(),
                onMovieSingleTap = { id, overview ->
                    onCardEvent(CardEventModel.OnSingleTap(id, overview))
                },
                onMovieDoubleTap = { id ->
                    val selectedMovie = movies.first { it.id == id }
                    onCardEvent(CardEventModel.OnDoubleTap(selectedMovie))
                }
            )
        }
    )
}

@Composable
fun ErrorMovieScreen(
    modifier: Modifier = Modifier,
    error: String,
) {
    Text(modifier = modifier, text = error)
}

@Composable
fun EmptyMovieScreen(modifier: Modifier = Modifier) {
    Text(modifier = modifier, text = "No data found")
}
