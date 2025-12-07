package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.EmptyStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.MovieDetailLoadingScreen
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.atoms.asStringSuspend
import com.msoula.hobbymatchmaker.core.design.models.EmptyStateConfig
import com.msoula.hobbymatchmaker.core.design.molecules.BackNavigationTopBar
import com.msoula.hobbymatchmaker.core.design.no_data
import com.msoula.hobbymatchmaker.core.design.not_found
import com.msoula.hobbymatchmaker.core.design.organisms.ActorSection
import com.msoula.hobbymatchmaker.core.design.organisms.MovieDetailInformation
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiEvent
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel

@Composable
fun MovieDetailContent(
    movieDetailViewModel: MovieDetailViewModel,
    onNavigate: (String) -> Unit
) {
    val movieDetailState by movieDetailViewModel.screenState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        movieDetailViewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar ->
                    snackBarHostState.showSnackbar(event.message.asStringSuspend())

                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = {
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
                BackNavigationTopBar(onBack = { onNavigate("movies") })
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            StateContainer(
                state = movieDetailState,
                onLoading = { MovieDetailLoadingScreen() },
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
                        onRetry = { movieDetailViewModel.observeMovieDetail() }
                    )
                },
                onSuccess = { movie ->
                    var videoPlayerVisible by rememberSaveable { mutableStateOf(false) }
                    var videoId by rememberSaveable { mutableStateOf(movie.videoKey) }
                    var isLoading by rememberSaveable { mutableStateOf(false) }

                    val filteredCast =
                        remember(movie.cast) { movie.cast.filterNot { it.key == "NO_CAST" } }

                    LaunchedEffect(movie.id) {
                        videoId = movie.videoKey
                        videoPlayerVisible = false
                        isLoading = false
                    }

                    LaunchedEffect(Unit) {
                        movieDetailViewModel.events.collect { event ->
                            when (event) {
                                is UiEvent.OnDataReady -> {
                                    videoId = event.data
                                    videoPlayerVisible = true
                                }

                                else -> {}
                            }
                        }
                    }

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
                            movieDetailViewModel.onEvent(
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
        }
    }
}
