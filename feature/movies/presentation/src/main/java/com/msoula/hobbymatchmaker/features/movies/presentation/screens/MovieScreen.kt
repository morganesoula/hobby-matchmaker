package com.msoula.hobbymatchmaker.features.movies.presentation.screens

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
import com.msoula.hobbymatchmaker.features.movies.presentation.CardEvent
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieItem
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel

@Composable
fun MovieScreen(
    modifier: Modifier = Modifier,
    movies: List<MovieUiModel>,
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
            MovieUiModel(1, "", isFavorite = false, playFavoriteAnimation = false),
            MovieUiModel(2, "", isFavorite = false, playFavoriteAnimation = false),
            MovieUiModel(3, "", isFavorite = false, playFavoriteAnimation = false),
            MovieUiModel(4, "", isFavorite = false, playFavoriteAnimation = false),
        ),
        onCardEvent = {},
    )
}
