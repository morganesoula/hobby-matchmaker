package com.msoula.hobbymatchmaker.features.movies.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.msoula.hobbymatchmaker.core.design.component.HMMShimmerEffect
import com.msoula.hobbymatchmaker.features.movies.presentation.Res
import com.msoula.hobbymatchmaker.features.movies.presentation.ic_movie_clapper_board
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import kotlinx.coroutines.delay
import okio.Path.Companion.toPath
import kotlin.math.abs

@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movie: MovieUiModel,
    index: Int,
    state: LazyListState,
    onCardEvent: (CardEventModel) -> Unit
) {
    val painter = rememberAsyncImagePainter(
        model = if (movie.coverFilePath.isEmpty()) {
            ImageRequest.Builder(LocalPlatformContext.current)
                .data(
                    Res.drawable.ic_movie_clapper_board
                ).size(coil3.size.Size(150, 150))
                .build()
        } else {
            ImageRequest.Builder(LocalPlatformContext.current)
                .data(movie.coverFilePath.toPath())
                .size(coil3.size.Size.ORIGINAL)
                .listener(
                    onStart = { print("\"\uD83C\uDFAC Start loading image\"") },
                    onSuccess = { _, _ -> print("✅ Success loading image") },
                    onError = { _, result ->
                        print(
                            "❌ Error loading image: " +
                                "${result.throwable.message}"
                        )
                    })
                .build()
        }
    )

    if (painter.state is AsyncImagePainter.State.Loading) {
        HMMShimmerEffect(isLoading = true) {}
    } else {
        HMMShimmerEffect(isLoading = false) {
            MovieItemContent(
                modifier = modifier,
                state = state,
                index = index,
                movie = movie,
                onCardEvent = onCardEvent,
                painter = painter
            )
        }
    }
}

@Composable
fun MovieItemContent(
    modifier: Modifier = Modifier,
    state: LazyListState,
    index: Int,
    movie: MovieUiModel,
    onCardEvent: (CardEventModel) -> Unit,
    painter: AsyncImagePainter
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
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            MovieItemContentCard(modifier, movie, onCardEvent, painter)
        }
    }
}

@Composable
fun MovieItemContentCard(
    modifier: Modifier = Modifier,
    movie: MovieUiModel,
    onCardEvent: (CardEventModel) -> Unit,
    painter: AsyncImagePainter
) {
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
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .clip(RoundedCornerShape(5))
                .fillMaxSize()
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

        Text(
            text = movie.title,
            color = Color.White,
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
                tint = Color.Red.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .scale(bigHeartScale)
            )
        }

        IconButton(
            onClick = {
                animateFavorite = true
                onCardEvent(CardEventModel.OnDoubleTap(movie))
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = CircleShape
                )
                .scale(favoriteScale)
        ) {
            Icon(
                imageVector = if (movie.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (movie.isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (movie.isFavorite) Color.Red else Color.White,
            )
        }
    }
}
