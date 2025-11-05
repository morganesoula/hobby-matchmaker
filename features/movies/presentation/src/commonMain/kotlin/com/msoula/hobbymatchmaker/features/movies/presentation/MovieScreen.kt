package com.msoula.hobbymatchmaker.features.movies.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Announcement
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.isIosPlatform
import com.msoula.hobbymatchmaker.core.design.CallOnceEffect
import com.msoula.hobbymatchmaker.core.design.ObserveEvents
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.SnackEffect
import com.msoula.hobbymatchmaker.core.design.component.LoadingCircularProgress
import com.msoula.hobbymatchmaker.core.design.no_fetching_detail_possible
import com.msoula.hobbymatchmaker.core.design.util.UIText
import com.msoula.hobbymatchmaker.core.design.util.asString
import com.msoula.hobbymatchmaker.features.movies.presentation.components.MovieItem
import com.msoula.hobbymatchmaker.features.movies.presentation.models.CardEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiEventModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import kotlinx.coroutines.flow.Flow

@Composable
fun MovieContent(
    modifier: Modifier = Modifier,
    movieViewModel: MovieViewModel,
    movieState: MovieUiStateModel,
    oneTimeEventChannelFlow: Flow<MovieUiEventModel>,
    redirectToMovieDetail: (Long) -> Unit,
    redirectToAuth: () -> Unit,
    redirectToProfile: () -> Unit
) {
    when (movieState) {
        is MovieUiStateModel.Success -> {
            MovieScreenContent(
                movies = movieState.list,
                oneTimeEventChannelFlow = oneTimeEventChannelFlow,
                redirectToMovieDetail = redirectToMovieDetail,
                onCardEvent = movieViewModel::onCardEvent,
                logOut = { movieViewModel.logOut() },
                redirectToAuth = redirectToAuth,
                redirectToProfile = redirectToProfile
            )
        }

        is MovieUiStateModel.Empty -> EmptyMovieScreen()
        is MovieUiStateModel.Error -> ErrorMovieScreen(error = movieState.errorMessage.asString())
        else -> LoadingCircularProgress()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreenContent(
    modifier: Modifier = Modifier,
    movies: List<MovieUiModel>,
    oneTimeEventChannelFlow: Flow<MovieUiEventModel>,
    redirectToMovieDetail: (movieId: Long) -> Unit,
    logOut: () -> Unit,
    onCardEvent: (CardEventModel) -> Unit,
    redirectToAuth: () -> Unit,
    redirectToProfile: () -> Unit
) {
    val listState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }

    ObserveEvents(oneTimeEventChannelFlow) { event ->
        when (event) {
            is MovieUiEventModel.OnMovieDetailClicked ->
                CallOnceEffect(event) {
                    Logger.d("Redirecting to movie detail as asked")
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
        },
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent
                ),
                actions = {
                    Row {
                        IconButton(
                            onClick = { redirectToProfile() },
                            content = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Announcement,
                                    contentDescription = "Profile",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        )

                        IconButton(
                            onClick = { logOut() },
                            content = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.Logout,
                                    contentDescription = "Logout",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        )
                    }
                }
            )

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
