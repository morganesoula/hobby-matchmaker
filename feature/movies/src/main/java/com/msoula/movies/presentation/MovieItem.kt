package com.msoula.movies.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.msoula.component.HMMShimmerEffect
import com.msoula.component.HMMSwipeCard
import com.msoula.component.event.SwipeCardEvent
import com.msoula.movies.data.model.Movie

private const val IMG_PREFIX = "https://image.tmdb.org/t/p/w500"

@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movie: Movie,
    onSwipeLeft: (event: SwipeCardEvent) -> Unit
) {
    val context = LocalContext.current

    val painter = rememberAsyncImagePainter(
        model =
        ImageRequest.Builder(context)
            .data(IMG_PREFIX + movie.posterJPG)
            .size(400, 450)
            .build()
    )

    HMMSwipeCard(
        modifier = modifier,
        onSwipeLeft = { onSwipeLeft(SwipeCardEvent.OnSwipeLeft(movie.id)) }) {
        HMMShimmerEffect(isLoading = false) {
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(450.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Image(
                    painter = painter, contentDescription = null, modifier = Modifier
                        .width(400.dp)
                        .height(450.dp)
                )
            }
        }
    }
}
