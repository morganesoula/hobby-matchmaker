package com.msoula.hobbymatchmaker.features.movies.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.msoula.component.HMMShimmerEffect
import com.msoula.hobbymatchmaker.features.movies.presentation.R
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.abs

@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movie: MovieUiModel,
    index: Int,
    state: LazyListState,
    onCardEvent: (CardEventModel) -> Unit,
    playAnimation: Boolean,
    shouldPlayHeartAnimation: (Boolean) -> Unit
) {
    LaunchedEffect(playAnimation) {
        if (playAnimation) {
            delay(2000)
            shouldPlayHeartAnimation(false)
        }
    }

    val painter = rememberAsyncImagePainter(
        model = if (movie.coverFilePath.isEmpty()) ImageRequest.Builder(LocalContext.current).data(
            R.drawable.ic_movie_clapper_board
        ).size(coil.size.Size(150, 150)).build()
        else ImageRequest.Builder(LocalContext.current).data(File(movie.coverFilePath))
            .size(coil.size.Size.ORIGINAL).build()
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

    // Like animation
    val heartColor by animateColorAsState(
        targetValue = Color.Red,
        animationSpec = tween(durationMillis = 2000, easing = FastOutLinearInEasing),
        label = "define heart color",
    )

    val heartSize by animateDpAsState(
        targetValue = if (movie.playFavoriteAnimation) 60.dp else 0.dp,
        animationSpec = tween(2000),
        label = "define heart size"
    )

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(movie) {
                        detectTapGestures(onDoubleTap = {
                            onCardEvent(CardEventModel.OnDoubleTap(movie))
                        })
                    }
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RoundedCornerShape(5))
                        .fillMaxSize()
                )

                if (movie.isFavorite) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(heartSize)
                            .padding(top = 5.dp, end = 5.dp),
                        contentDescription = "heart icon",
                        tint = heartColor
                    )
                }
            }
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
