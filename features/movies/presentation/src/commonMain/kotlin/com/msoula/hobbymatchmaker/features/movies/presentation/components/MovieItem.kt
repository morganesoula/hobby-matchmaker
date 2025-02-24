package com.msoula.hobbymatchmaker.features.movies.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
        }
        else ImageRequest.Builder(LocalPlatformContext.current)
            .data(movie.coverFilePath.toPath())
            .size(coil3.size.Size.ORIGINAL)
            .build()
    )

    if (painter.state.value is AsyncImagePainter.State.Loading) {
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
                .width(300.dp)
                .height(440.dp)
                .padding(end = 10.dp)
                .scale(scale)
                .zIndex(scale * 10),
            shape = RoundedCornerShape(5),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            MovieItemContentCard(modifier, movie, onCardEvent, painter)
        }
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = movie.title,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium
        )
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
    var pulse by remember { mutableStateOf(false) }
    val animationDelay = 600L

    val heartScaleAnimation by animateFloatAsState(
        targetValue = if (showBigHeart || pulse) 2f else 1f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing), label = ""
    )

    LaunchedEffect(showBigHeart, pulse) {
        if (showBigHeart) {
            delay(animationDelay)
            showBigHeart = false
        }

        if (pulse) {
            delay(animationDelay)
            pulse = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(movie) {
                detectTapGestures(
                    onDoubleTap = {
                        showBigHeart = true
                        pulse = true
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

        if (showBigHeart) {
            Icon(
                imageVector = Icons.Default.Favorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .scale(heartScaleAnimation)
                    .padding(top = 10.dp, end = 10.dp),
                contentDescription = "heart icon",
                tint = Color.Red
            )
        }
        Icon(
            imageVector = if (movie.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = "Like",
            tint = if (movie.isFavorite) Color.Red else Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .scale(heartScaleAnimation)
                .padding(top = 10.dp, end = 10.dp)
        )
    }
}
