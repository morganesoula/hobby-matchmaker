package com.msoula.hobbymatchmaker.features.movies.presentation

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
import com.msoula.hobbymatchmaker.core.common.ObserveAsEvents
import com.msoula.hobbymatchmaker.features.movies.presentation.components.MovieItem
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieNavigationModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun MovieScreen(
    modifier: Modifier = Modifier,
    movies: List<MovieUiModel>,
    oneTimeEventChannelFlow: Flow<MovieNavigationModel>,
    redirectToMovieDetail: (movieId: Long) -> Unit,
    onCardEvent: (CardEventModel) -> Unit
) {
    val listState = rememberLazyListState()

    ObserveAsEvents(flow = oneTimeEventChannelFlow) { event ->
        when (event) {
            is MovieNavigationModel.OnMovieDetailClicked -> {
                redirectToMovieDetail(event.movieId)
            }
        }
    }

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
    Text(modifier = modifier, text = error)
}

@Composable
fun EmptyMovieScreen(modifier: Modifier = Modifier) {
    Text(modifier = modifier, text = "No data found")
}

@Preview
@Composable
fun MovieScreenPreview() {
    MovieScreen(
        movies = listOf(
            MovieUiModel(
                1,
                "",
                isFavorite = false,
                playFavoriteAnimation = false,
                title = "Title 1",
                overview = ""
            ),
            MovieUiModel(
                2,
                "",
                isFavorite = false,
                playFavoriteAnimation = false,
                title = "Title 2",
                overview = ""
            ),
            MovieUiModel(
                3,
                "",
                isFavorite = false,
                playFavoriteAnimation = false,
                title = "Title 3",
                overview = ""
            ),
            MovieUiModel(
                4,
                "",
                isFavorite = false,
                playFavoriteAnimation = false,
                title = "Title 4",
                overview = ""
            )
        ),
        onCardEvent = {},
        oneTimeEventChannelFlow = flowOf(),
        redirectToMovieDetail = {}
    )
}
