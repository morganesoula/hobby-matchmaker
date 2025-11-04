package com.msoula.hobbymatchmaker.features.movies.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.formatOneDecimal
import com.msoula.hobbymatchmaker.core.design.component.ErrorPosterPlaceholder
import com.msoula.hobbymatchmaker.core.design.component.HMMShimmerEffect
import com.msoula.hobbymatchmaker.core.design.component.LoadingPosterPlaceholder
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import kotlin.math.abs

@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movie: MovieUiModel,
    index: Int,
    state: LazyListState,
    onCardEvent: (CardEventModel) -> Unit
) {
    MovieItemContent(
        modifier = modifier,
        state = state,
        index = index,
        movie = movie,
        onCardEvent = onCardEvent
    )
}

@Composable
fun MovieItemContent(
    modifier: Modifier = Modifier,
    state: LazyListState,
    index: Int,
    movie: MovieUiModel,
    onCardEvent: (CardEventModel) -> Unit
) {
    val scale by remember {
        derivedStateOf {
            val currentItem =
                state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                    ?: return@derivedStateOf 1.0f
            val halfRowWidth = state.layoutInfo.viewportSize.width / 2
            (
                1f - minOf(
                    1f,
                    abs(currentItem.offset + (currentItem.size / 2) - halfRowWidth).toFloat() / halfRowWidth,
                ) * 0.10f
                )
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = modifier
                .semantics(mergeDescendants = true) {
                    contentDescription = "${movie.title}, ${
                        if (movie.isFavorite) "favorite"
                        else "not favorite"
                    }"
                }
                .width(300.dp)
                .height(440.dp)
                .padding(end = 10.dp)
                .scale(scale)
                .zIndex(scale * 10),
            shape = RoundedCornerShape(5),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (scale > 1f) 12.dp else 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            MovieItemContentCard(modifier, movie, onCardEvent)
        }
    }
}

@Composable
fun MovieItemContentCard(
    modifier: Modifier = Modifier,
    movie: MovieUiModel,
    onCardEvent: (CardEventModel) -> Unit
) {
    val imageLoader = rememberCoilImageLoader()

    var showBigHeart by remember { mutableStateOf(false) }
    var animateFavorite by remember { mutableStateOf(false) }

    val bigHeartScale by animateFloatAsState(
        targetValue = if (showBigHeart) 2f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = ""
    )

    val favoriteScale by animateFloatAsState(
        targetValue = if (animateFavorite) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
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
        modifier = modifier
            .fillMaxSize()
            .pointerInput(movie) {
                detectTapGestures(
                    onDoubleTap = {
                        showBigHeart = true
                        onCardEvent(CardEventModel.OnDoubleTap(movie))
                    },
                    onTap = {
                        onCardEvent(CardEventModel.OnSingleTap(movie.id, movie.overview))
                    }
                )
            }
    ) {
        SubcomposeAsyncImage(
            imageLoader = imageLoader,
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(movie.coverFilePath)
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
            RatingChip(movie.note)
            Spacer(Modifier.weight(1f))
            FavoriteButton(
                movie.isFavorite,
                favoriteScale,
                onClick = {
                    animateFavorite = true
                    onCardEvent(CardEventModel.OnDoubleTap(movie))
                }
            )
        }

        Text(
            text = movie.title,
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
                imageVector = Icons.Default.Favorite,
                contentDescription = "heart icon",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .scale(bigHeartScale)
            )
        }
    }
}

@Composable
fun FavoriteButton(isFavorite: Boolean, scale: Float, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .background(color = Color.Black.copy(alpha = 0.3f), shape = CircleShape)
            .scale(scale)
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun RatingChip(voteAverage: Double, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.width(4.dp))
        Text(voteAverage.formatOneDecimal(), style = MaterialTheme.typography.labelLarge)
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
