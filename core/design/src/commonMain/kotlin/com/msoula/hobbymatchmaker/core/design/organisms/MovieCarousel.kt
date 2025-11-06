package com.msoula.hobbymatchmaker.core.design.organisms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.molecules.MovieCard
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

/**
 * Simple data class representing movie data for the carousel
 * This keeps the design module completely independent from feature modules
 */
data class MovieCarouselItem(
    val id: Long,
    val title: String,
    val overview: String,
    val coverFilePath: String,
    val note: Double,
    val isFavorite: Boolean
)

/**
 * Carousel component displaying a horizontal scrollable list of movie cards
 *
 * @param modifier Modifier to apply to the root component
 * @param padding PaddingValues from parent Scaffold
 * @param movies List of movie items to display
 * @param onMovieSingleTap Callback when a movie card is tapped once
 * @param onMovieDoubleTap Callback when a movie card is double-tapped (favorite)
 */
@Composable
fun MovieCarousel(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    movies: List<MovieCarouselItem>,
    onMovieSingleTap: (movieId: Long, overview: String) -> Unit,
    onMovieDoubleTap: (id: Long) -> Unit
) {
    val listState = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                top = if (isIosPlatform()) padding.calculateTopPadding() - 8.dp else padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding(),
                start = padding.calculateStartPadding(LocalLayoutDirection.current),
                end = padding.calculateEndPadding(LocalLayoutDirection.current)
            ),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            contentPadding = PaddingValues(start = CustomSize.SixtyFour),
            verticalAlignment = Alignment.CenterVertically,
            state = listState
        ) {
            itemsIndexed(movies, key = { _, movie -> movie.id }) { index, currentMovie ->
                MovieCard(
                    movieId = currentMovie.id,
                    title = currentMovie.title,
                    overview = currentMovie.overview,
                    posterFilePath = currentMovie.coverFilePath,
                    voteAverage = currentMovie.note,
                    isFavorite = currentMovie.isFavorite,
                    onDoubleTap = onMovieDoubleTap,
                    onSingleTap = onMovieSingleTap,
                    state = listState,
                    index = index
                )
            }
        }
    }
}
