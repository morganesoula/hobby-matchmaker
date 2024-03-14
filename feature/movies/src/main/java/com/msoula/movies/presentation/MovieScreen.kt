package com.msoula.movies.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.msoula.movies.presentation.model.MovieUi

@Composable
fun MovieScreen(
    modifier: Modifier = Modifier,
    movies: List<MovieUi>,
    onCardEvent: (CardEvent) -> Unit,
) {
    val listState = rememberLazyListState()

    //TODO update two last playAnimation / shouldPlayHeartAnimation
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = 60.dp),
        verticalAlignment = Alignment.CenterVertically,
        state = listState,
    ) {
        itemsIndexed(movies, key = { _, movie -> movie.id }) { index, currentMovie ->
            MovieItem(
                movie = currentMovie,
                index = index,
                onCardEvent = onCardEvent,
                state = listState,
                playAnimation = false,
                shouldPlayHeartAnimation = {}
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
                MovieUi(1, "", isFavorite = false, playFavoriteAnimation = false),
                MovieUi(2, "", isFavorite = false, playFavoriteAnimation = false),
                MovieUi(3, "", isFavorite = false, playFavoriteAnimation = false),
                MovieUi(4, "", isFavorite = false, playFavoriteAnimation = false),
            ),
        onCardEvent = {},
    )
}
