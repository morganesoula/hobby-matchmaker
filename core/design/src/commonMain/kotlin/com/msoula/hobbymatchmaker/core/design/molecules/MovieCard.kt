package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.design.atoms.ErrorPosterPlaceholder
import com.msoula.hobbymatchmaker.core.design.atoms.FavoriteButton
import com.msoula.hobbymatchmaker.core.design.atoms.HMMShimmerEffect
import com.msoula.hobbymatchmaker.core.design.atoms.LoadingPosterPlaceholder
import com.msoula.hobbymatchmaker.core.design.atoms.MovieGenericCard
import com.msoula.hobbymatchmaker.core.design.atoms.RatingChip
import com.msoula.hobbymatchmaker.core.design.icons.Heart
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun MovieCard(
    modifier: Modifier = Modifier,
    movieId: Long,
    title: String,
    overview: String,
    posterFilePath: String,
    voteAverage: Double,
    isFavorite: Boolean,
    onDoubleTap: (movieId: Long) -> Unit,
    onSingleTap: (id: Long, overview: String) -> Unit,
    state: LazyListState,
    index: Int
) {
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

    MovieGenericCard(state = state, index = index) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .pointerInput(movieId) {
                    detectTapGestures(
                        onDoubleTap = {
                            showBigHeart = true
                            onDoubleTap(movieId)
                        },
                        onTap = {
                            onSingleTap(movieId, overview)
                        }
                    )
                }
                .semantics {
                    role = Role.Button
                    stateDescription = if (isFavorite) "Favorited" else "Not favorited"
                }
        ) {
            SubcomposeAsyncImage(
                imageLoader = rememberCoilImageLoader(),
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(posterFilePath)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(5)),
                contentScale = ContentScale.Crop,
                loading = {
                    HMMShimmerEffect()
                    LoadingPosterPlaceholder()
                },
                error = {
                    Logger.e("Error while syncing poster image")
                    ErrorPosterPlaceholder()
                },
                success = { SubcomposeAsyncImageContent() }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .align(Alignment.TopCenter),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RatingChip(voteAverage = voteAverage)
                Spacer(Modifier.weight(1f))
                FavoriteButton(
                    isFavorite = isFavorite,
                    animateFavorite = animateFavorite,
                    onClick = {
                        animateFavorite = true
                        onDoubleTap(movieId)
                    }
                )
            }

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = 12.dp)
            )

            if (showBigHeart) {
                Icon(
                    imageVector = Heart,
                    contentDescription = "heart icon",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .scale(bigHeartScale)
                )
            }
        }
    }
}

@Composable
fun rememberCoilImageLoader(): ImageLoader {
    val context = LocalPlatformContext.current
    val httpClient: HttpClient = koinInject()

    return remember {
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory(httpClient))
            }
            .build()
    }
}
