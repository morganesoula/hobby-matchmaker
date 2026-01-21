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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.atoms.LoadMoreDataIndicator
import com.msoula.hobbymatchmaker.core.design.models.MovieCarouselItem
import com.msoula.hobbymatchmaker.core.design.molecules.MovieCard
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MovieCarousel(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    movies: ImmutableList<MovieCarouselItem>,
    isLoadingMore: Boolean = false,
    hasMorePages: Boolean = true,
    onLoadMore: () -> Unit = {},
    onMovieSingleTap: (movieId: Long, overview: String) -> Unit,
    onMovieDoubleTap: (id: Long) -> Unit
) {
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleIndex?.let {
                it >= totalItems - 3 && hasMorePages && !isLoadingMore && totalItems > 0
            }
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore == true) onLoadMore()
    }

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

            if (hasMorePages) {
                item(key = "load_more_indicator") {
                    LoadMoreDataIndicator(
                        isLoading = isLoadingMore,
                        onLoadMore = onLoadMore
                    )
                }
            }
        }
    }
}
