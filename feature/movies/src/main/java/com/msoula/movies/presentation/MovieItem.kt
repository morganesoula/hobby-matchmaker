package com.msoula.movies.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.msoula.component.HMMShimmerEffect
import com.msoula.movies.presentation.model.MovieUi
import java.io.File
import kotlin.math.abs

@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movie: MovieUi,
    index: Int,
    state: LazyListState,
    onCardEvent: (CardEvent) -> Unit,
) {
    val painter = rememberAsyncImagePainter(model = File(movie.coverUrl))

    // Row displaying
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

    HMMShimmerEffect(modifier = modifier, isLoading = false) {
        Card(
            modifier =
                Modifier
                    .width(300.dp)
                    .height(440.dp)
                    .padding(end = 10.dp)
                    .scale(scale)
                    .zIndex(scale * 10),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .pointerInput(movie) {
                            detectTapGestures(onDoubleTap = {
                                onCardEvent(CardEvent.OnDoubleTap(movie))
                            })
                        },
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )

                if (movie.isFavorite) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "heart icon visible",
                        modifier =
                            Modifier
                                .size(60.dp)
                                .align(Alignment.Center),
                        tint = heartColor,
                    )
                }
            }
        }
    }
}
