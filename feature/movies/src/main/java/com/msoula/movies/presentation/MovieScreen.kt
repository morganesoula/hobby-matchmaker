package com.msoula.movies.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.msoula.movies.data.model.MovieUi

@Composable
fun MovieScreen(
    modifier: Modifier = Modifier,
    movies: List<MovieUi>,
    onCardEvent: (CardEvent) -> Unit,
) {
    val state = rememberLazyListState()

    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        state = state,
    ) {
        items(movies) { movie ->
            if (movie != movies[0]) {
                Spacer(modifier = Modifier.width(10.dp))
            } else {
                Spacer(
                    modifier =
                        Modifier.width(
                            50.dp,
                        ),
                )
            }

            MovieItem(
                movie = movie,
                state = state,
                index = movies.indexOf(movie),
                onCardEvent = onCardEvent,
            )
        }
    }
}

@Composable
fun ErrorMovieScreen(
    modifier: Modifier = Modifier,
    error: String,
) {
    Text(modifier = modifier, text = "Error while fetching: $error")
}

@Composable
fun EmptyMovieScreen(modifier: Modifier = Modifier) {
    Text(modifier = modifier, text = "No data found")
}

@Preview
@Composable
fun MovieScreenPreview() {
    MovieScreen(
        movies =
            listOf(
                MovieUi(1, "", isFavorite = false),
                MovieUi(2, "", isFavorite = false),
                MovieUi(3, "", isFavorite = false),
                MovieUi(4, "", isFavorite = false),
            ),
        onCardEvent = {},
    )
}
