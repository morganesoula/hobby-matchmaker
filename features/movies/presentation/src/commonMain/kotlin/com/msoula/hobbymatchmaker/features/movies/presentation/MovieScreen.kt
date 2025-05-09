package com.msoula.hobbymatchmaker.features.movies.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.msoula.hobbymatchmaker.core.common.ObserveAsEvents
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.component.HMMHomeTopBar
import com.msoula.hobbymatchmaker.features.movies.presentation.components.MovieItem
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

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
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val noFetchingDetailPossibleMessage = stringResource(Res.string.no_fetching_detail_possible)

    ObserveAsEvents(flow = oneTimeEventChannelFlow) { event ->
        coroutineScope.launch {
            when (event) {
                is MovieUiEventModel.OnMovieDetailClicked -> {
                    redirectToMovieDetail(event.movieId)
                }

                is MovieUiEventModel.OnMovieUiFetchedError -> {
                    snackBarHostState.showSnackbar(message = event.error)
                }

                is MovieUiEventModel.OnLogOutSuccess -> redirectToAuth()

                is MovieUiEventModel.NoFetchingDetailPossible -> {
                    snackBarHostState.showSnackbar(noFetchingDetailPossibleMessage)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                Snackbar(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                    Text(text = data.visuals.message)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = if (isIosPlatform()) padding.calculateTopPadding() - 8.dp else padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding(),
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    end = padding.calculateStartPadding(LocalLayoutDirection.current)
                )
                .zIndex(0f),
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
