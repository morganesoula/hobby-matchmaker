package com.msoula.hobbymatchmaker.features.movies.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.CallOnceEffect
import com.msoula.hobbymatchmaker.core.common.ObserveEvents
import com.msoula.hobbymatchmaker.core.common.SnackEffect
import com.msoula.hobbymatchmaker.core.common.UIText
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.component.HMMHomeTopBar
import com.msoula.hobbymatchmaker.core.design.no_fetching_detail_possible
import com.msoula.hobbymatchmaker.features.movies.presentation.components.MovieItem
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import kotlinx.coroutines.flow.Flow

@Composable
fun MovieScreenContent(
    modifier: Modifier = Modifier,
    movies: List<MovieUiModel>,
    oneTimeEventChannelFlow: Flow<MovieUiEventModel>,
    redirectToMovieDetail: (movieId: Long) -> Unit,
    logOut: () -> Unit,
    onCardEvent: (CardEventModel) -> Unit,
    redirectToAuth: () -> Unit
) {
    val listState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }

    ObserveEvents(oneTimeEventChannelFlow) { event ->
        when (event) {
            is MovieUiEventModel.OnMovieDetailClicked ->
                CallOnceEffect(event) {
                    redirectToMovieDetail(event.movieId)
                }

            is MovieUiEventModel.OnLogOutSuccess ->
                CallOnceEffect(event) {
                    redirectToAuth()
                }

            is MovieUiEventModel.OnMovieUiFetchedError ->
                SnackEffect(snackBarHostState, event.error, event)

            is MovieUiEventModel.OnLogOutFailure ->
                SnackEffect(snackBarHostState, event.error, event)

            is MovieUiEventModel.ShowError ->
                SnackEffect(snackBarHostState, event.error, event)

            is MovieUiEventModel.NoFetchingDetailPossible ->
                SnackEffect(
                    snackBarHostState,
                    UIText.Resource(Res.string.no_fetching_detail_possible),
                    event
                )
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                Snackbar(
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                        .padding(horizontal = 16.dp).fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = data.visuals.message)
                }
            }
        }
    ) { padding ->
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
                        state = listState
                    )
                }
            }

            HMMHomeTopBar { logOut() }
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
