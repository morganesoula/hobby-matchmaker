package com.msoula.movies.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.msoula.component.event.SwipeCardEvent
import com.msoula.movies.data.model.Movie

@Composable
fun MovieScreen(
    modifier: Modifier = Modifier,
    movies: List<Movie>,
    onSwipeLeft: (event: SwipeCardEvent) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp)
    ) {
        for (movie in movies) {
            MovieItem(
                movie = movie,
                modifier = Modifier.align(Alignment.Center),
                onSwipeLeft = onSwipeLeft
            )
        }
    }
}

@Composable
fun EmptyMovieScreen(modifier: Modifier = Modifier, errorMessage: String) {
    Text(modifier = modifier, text = "Error: $errorMessage")
}
