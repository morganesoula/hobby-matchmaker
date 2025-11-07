package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.CallOnceEffect
import com.msoula.hobbymatchmaker.core.design.ObserveEvents
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.SnackEffect
import com.msoula.hobbymatchmaker.core.design.component.LoadingCircularProgress
import com.msoula.hobbymatchmaker.core.design.component.LoadingOverlay
import com.msoula.hobbymatchmaker.core.design.connection_issue
import com.msoula.hobbymatchmaker.core.design.molecules.BackNavigationTopBar
import com.msoula.hobbymatchmaker.core.design.no_data
import com.msoula.hobbymatchmaker.core.design.no_trailer_available
import com.msoula.hobbymatchmaker.core.design.organisms.ActorSection
import com.msoula.hobbymatchmaker.core.design.organisms.MovieDetailInformation
import com.msoula.hobbymatchmaker.core.design.templates.MovieDetailLayout
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.asString
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailViewStateModel
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource

@Composable
fun MovieDetailContent(
    viewState: MovieDetailViewStateModel,
    oneTimeEventFlow: Flow<MovieDetailUiEventModel>,
    onPlayTrailerClicked: (event: MovieDetailUiEventModel) -> Unit,
    onMovieDetailBackPressed: () -> Unit
) {
    when (viewState) {
        is MovieDetailViewStateModel.Error -> ErrorMovieDetailScreen(error = viewState.error)
        is MovieDetailViewStateModel.Loading -> LoadingCircularProgress()
        is MovieDetailViewStateModel.Empty -> EmptyMovieDetailScreen()
        is MovieDetailViewStateModel.Success ->
            MovieDetailScreen(
                oneTimeEventFlow = oneTimeEventFlow,
                movie = viewState.movie,
                onPlayTrailerClicked = onPlayTrailerClicked,
                onMovieDetailBackPressed = onMovieDetailBackPressed
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    modifier: Modifier = Modifier,
    oneTimeEventFlow: Flow<MovieDetailUiEventModel>,
    movie: MovieDetailUiModel,
    onPlayTrailerClicked: (event: MovieDetailUiEventModel) -> Unit,
    onMovieDetailBackPressed: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }

    var videoPlayerVisible by rememberSaveable { mutableStateOf(false) }
    var videoId by rememberSaveable { mutableStateOf(movie.videoKey) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    val filteredCast = remember(movie.cast) { movie.cast.filterNot { it.key == "NO_CAST" } }

    LaunchedEffect(movie.id) {
        videoId = movie.videoKey
        videoPlayerVisible = false
        isLoading = false
    }

    ObserveEvents(oneTimeEventFlow) { event ->
        when (event) {
            is MovieDetailUiEventModel.OnMovieDetailUiFetchedError ->
                SnackEffect(snackBarHostState, event.error, event)

            is MovieDetailUiEventModel.OnPlayMovieTrailerReady ->
                CallOnceEffect(event) {
                    if (isLoading) isLoading = false
                    videoId = event.movieUri
                    videoPlayerVisible = true
                }

            is MovieDetailUiEventModel.ErrorFetchingTrailer -> {
                if (isLoading) isLoading = false
                SnackEffect(
                    snackBarHostState,
                    UIText.Resource(Res.string.no_trailer_available),
                    event
                )
            }

            is MovieDetailUiEventModel.NoConnection ->
                SnackEffect(
                    snackBarHostState,
                    UIText.Resource(Res.string.connection_issue),
                    event
                )

            is MovieDetailUiEventModel.LoadingTrailer -> isLoading = true
            else -> Unit
        }
    }

    MovieDetailLayout(
        snackBarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = data.visuals.message)
                    }
                }
            )
        },
        topBar = {
            if (isIosPlatform()) {
                BackNavigationTopBar(onBack = onMovieDetailBackPressed)
            }
        },
        movieDetailSection = { padding ->
            MovieDetailInformation(
                padding = padding,
                posterPath = movie.posterPath,
                status = movie.status,
                title = movie.title,
                releaseDate = movie.releaseDate,
                genres = movie.genre,
                duration = movie.duration,
                videoId = videoId,
                movieId = movie.id,
                isVideoUriKnown = videoId.isNotEmpty(),
                overview = movie.synopsis,
                filteredCast = filteredCast,
                isLoading = isLoading,
                videoPlayerVisible = videoPlayerVisible,
                onVideoPlayerDismissed = { videoPlayerVisible = false },
                onPlayTrailerClicked = { localMovieId, localVideoUriKnown ->
                    onPlayTrailerClicked(
                        MovieDetailUiEventModel.OnPlayMovieTrailerClicked(
                            movieId = localMovieId,
                            isVideoURIknown = localVideoUriKnown
                        )
                    )
                },
                actorSection = {
                    ActorSection(cast = movie.cast)
                }
            )
        }
    )

    LoadingOverlay(isLoading)
}

@Composable
fun ErrorMovieDetailScreen(error: UIText) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = error.asString(), style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun EmptyMovieDetailScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(Res.string.no_data),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
