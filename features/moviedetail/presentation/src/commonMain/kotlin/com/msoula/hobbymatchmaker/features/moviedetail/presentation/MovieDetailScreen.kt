package com.msoula.hobbymatchmaker.features.moviedetail.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.EmptyStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorStateScreen
import com.msoula.hobbymatchmaker.core.design.atoms.MovieDetailLoadingScreen
import com.msoula.hobbymatchmaker.core.design.atoms.StateContainer
import com.msoula.hobbymatchmaker.core.design.icons.MaterialIconsMovie_filter
import com.msoula.hobbymatchmaker.core.design.models.Casting
import com.msoula.hobbymatchmaker.core.design.models.EmptyStateConfig
import com.msoula.hobbymatchmaker.core.design.molecules.BackNavigationTopBar
import com.msoula.hobbymatchmaker.core.design.no_data
import com.msoula.hobbymatchmaker.core.design.not_found
import com.msoula.hobbymatchmaker.core.design.organisms.ActorSection
import com.msoula.hobbymatchmaker.core.design.organisms.FriendSection
import com.msoula.hobbymatchmaker.core.design.organisms.MovieDetailInformation
import com.msoula.hobbymatchmaker.core.design.util.NavigationDestination
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.UiState
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.mappers.toProfileSocialMember
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiEventModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.VideoPlayerState
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MovieDetailContent(
    movieDetailState: UiState<MovieDetailUiModel>,
    snackBarHostState: SnackbarHostState,
    videoPlayerState: VideoPlayerState,
    onVideoPlayerDismissed: () -> Unit,
    onNavigate: (NavigationDestination) -> Unit,
    retryObservation: () -> Unit,
    onEvent: (MovieDetailUiEventModel) -> Unit
) {
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
                BackNavigationTopBar(onBack = { onNavigate(NavigationDestination.Movies) })
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
                            icon = MaterialIconsMovie_filter,
                            title = UIText.Resource(Res.string.no_data),
                            description = UIText.Resource(Res.string.not_found)
                        )
                    )
                },
                onError = { error, hint ->
                    ErrorStateScreen(
                        error = error,
                        hint = hint,
                        onRetry = retryObservation
                    )
                },
                onSuccess = { movie ->
                    val movieWithCast = movie.takeIf { it.hasCast }

                    MovieDetailInformation(
                        padding = padding,
                        posterPath = movie.posterPath,
                        status = movie.status,
                        title = movie.title,
                        isFavorite = movie.isFavorite,
                        releaseDate = movie.releaseDate,
                        genres = movie.genre.toImmutableList(),
                        duration = movie.duration,
                        videoId = videoPlayerState.videoId,
                        movieId = movie.id,
                        isVideoUriKnown = videoPlayerState.videoId.isNotEmpty(),
                        overview = movie.synopsis,
                        filteredCast = Casting(movieWithCast?.cast?.associate { (name, role) -> name to role }
                            ?: emptyMap()),
                        isLoading = videoPlayerState.isLoading,
                        videoPlayerVisible = videoPlayerState.isVisible,
                        onVideoPlayerDismissed = onVideoPlayerDismissed,
                        onMovieDoubleTap = {
                            onEvent(
                                MovieDetailUiEventModel.OnMovieDoubleTap(it)
                            )
                        },
                        onPlayTrailerClicked = { localMovieId, localVideoUriKnown ->
                            onEvent(
                                MovieDetailUiEventModel.OnPlayMovieTrailerClicked(
                                    movieId = localMovieId,
                                    isVideoURIknown = localVideoUriKnown
                                )
                            )
                        },
                        actorSection = {
                            ActorSection(casting = Casting(movie.cast.associate { (name, role) -> name to role }))
                        },
                        friendSection = {
                            FriendSection(movie.sharedMembers.map { member -> member.toProfileSocialMember() }
                                .toImmutableList())
                        }
                    )
                }
            )
        }
    }
}
