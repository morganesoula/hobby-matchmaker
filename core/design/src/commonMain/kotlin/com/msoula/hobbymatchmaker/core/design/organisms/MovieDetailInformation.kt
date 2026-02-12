package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.crossfade
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.atoms.FavoriteButton
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWidth8
import com.msoula.hobbymatchmaker.core.design.atoms.StatusChip
import com.msoula.hobbymatchmaker.core.design.atoms.VideoPlayer
import com.msoula.hobbymatchmaker.core.design.ic_no_image_found_playstore
import com.msoula.hobbymatchmaker.core.design.icons.FeatherPlayCircle
import com.msoula.hobbymatchmaker.core.design.models.Casting
import com.msoula.hobbymatchmaker.core.design.molecules.MovieInformationMetaPill
import com.msoula.hobbymatchmaker.core.design.molecules.MovieOverviewExpandable
import com.msoula.hobbymatchmaker.core.design.molecules.MovieTitleMetaPill
import com.msoula.hobbymatchmaker.core.design.movie_canceled
import com.msoula.hobbymatchmaker.core.design.movie_in_production
import com.msoula.hobbymatchmaker.core.design.movie_planned
import com.msoula.hobbymatchmaker.core.design.movie_post_production
import com.msoula.hobbymatchmaker.core.design.movie_released
import com.msoula.hobbymatchmaker.core.design.movie_rumored
import com.msoula.hobbymatchmaker.core.design.play_icon_accessibility
import com.msoula.hobbymatchmaker.core.design.play_trailer
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun MovieDetailInformation(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    posterPath: String?,
    status: String,
    title: String,
    isFavorite: Boolean,
    releaseDate: String,
    genres: ImmutableList<String>,
    duration: Int,
    videoId: String,
    movieId: Long,
    isVideoUriKnown: Boolean,
    overview: String,
    filteredCast: Casting,
    isLoading: Boolean,
    videoPlayerVisible: Boolean,
    onVideoPlayerDismissed: () -> Unit,
    onPlayTrailerClicked: (Long, Boolean) -> Unit,
    onMovieDoubleTap: (movieId: Long) -> Unit,
    actorSection: @Composable () -> Unit
) {
    val imageLoader: ImageLoader = koinInject()

    val context = LocalPlatformContext.current
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val scrollState = rememberScrollState()
    val scrim = rememberLegibilityScrim()

    var showBigHeart by remember { mutableStateOf(false) }
    var animateFavorite by remember { mutableStateOf(false) }

    val bigHeartScale by animateFloatAsState(
        targetValue = if (showBigHeart) 2f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = ""
    )

    LaunchedEffect(showBigHeart) {
        if (showBigHeart) {
            delay(600)
            showBigHeart = false
        }
    }

    LaunchedEffect(animateFavorite) {
        if (animateFavorite) {
            delay(300)
            animateFavorite = false
        }
    }

    Box(
        modifier
            .fillMaxSize()
            .padding(
                start = padding.calculateStartPadding(LocalLayoutDirection.current),
                end = padding.calculateEndPadding(LocalLayoutDirection.current),
                bottom = padding.calculateBottomPadding()
            )
    ) {
        posterPath?.let {
            AsyncImage(
                imageLoader = imageLoader,
                model = ImageRequest.Builder(context)
                    .data(it)
                    .crossfade(true)
                    .listener(
                        object : ImageRequest.Listener {
                            override fun onSuccess(
                                request: ImageRequest,
                                result: SuccessResult
                            ) {
                                Logger.d("detail bg loaded: ${request.data}")
                            }

                            override fun onError(request: ImageRequest, result: ErrorResult) {
                                Logger.e("detail bg error: ${request.data} → ${result.throwable.message}")
                            }
                        }
                    )
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationY = -scrollState.value * .2f },
                contentScale = ContentScale.Crop
            )
        } ?: run {
            Image(
                painter = painterResource(Res.drawable.ic_no_image_found_playstore),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Content on top of the image and below the main content
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(brush = scrim)
        )

        // Main Content
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(
                    top = 300.dp,
                    start = CustomSize.Sixteen,
                    end = CustomSize.Sixteen,
                    bottom = bottomInset + 24.dp
                )
                .fillMaxSize()
        ) {
            StatusChip(status = mapStatus(status))
            SpacerHeight8()
            MovieTitleMetaPill(title = title)
            SpacerHeight8()
            MovieInformationMetaPill(releaseDate, genres.toImmutableList(), duration)
            SpacerHeight16()

            if (videoPlayerVisible) {
                VideoPlayer(videoId = videoId, onDismiss = onVideoPlayerDismissed)
            } else {
                Button(
                    onClick = {
                        onPlayTrailerClicked(movieId, isVideoUriKnown)
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .shadow(8.dp, shape = RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = .2f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = FeatherPlayCircle,
                                contentDescription = stringResource(
                                    Res.string.play_icon_accessibility
                                ),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        SpacerWidth8()
                        Text(
                            text = stringResource(Res.string.play_trailer),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            SpacerHeight16()
            MovieOverviewExpandable(overview = overview)

            if (filteredCast.cast.isNotEmpty()) actorSection()
        }

        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(10.dp)
                .align(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FavoriteButton(
                isFavorite = isFavorite,
                animateFavorite = animateFavorite,
                onClick = {
                    Logger.d("detail favorite clicked")
                    animateFavorite = true
                    onMovieDoubleTap(movieId)
                }
            )
        }
    }
}

@Composable
private fun rememberLegibilityScrim(): Brush {
    val base = if (isSystemInDarkTheme())
        MaterialTheme.colorScheme.background
    else MaterialTheme.colorScheme.surface

    return remember(base) {
        Brush.verticalGradient(
            colorStops = arrayOf(
                0f to Color.Transparent,
                0.35f to base.copy(alpha = 0.55f),
                0.6f to base.copy(alpha = 0.82f),
                1f to base.copy(alpha = 0.95f)
            )
        )
    }
}

@Composable
private fun mapStatus(status: String): String =
    when (status.trim()) {
        "Rumored" -> stringResource(Res.string.movie_rumored)
        "Planned" -> stringResource(Res.string.movie_planned)
        "In Production" -> stringResource(Res.string.movie_in_production)
        "Post Production" -> stringResource(Res.string.movie_post_production)
        "Released" -> stringResource(Res.string.movie_released)
        "Canceled" -> stringResource(Res.string.movie_canceled)
        else -> status
    }
