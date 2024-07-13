package com.msoula.hobbymatchmaker.feature.moviedetail.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.msoula.hobbymatchmaker.core.common.ObserveAsEvents
import com.msoula.hobbymatchmaker.core.design.component.LocalSnackBar
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.MovieDetailEvent
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.models.MovieDetailUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun MovieDetailScreen(
    modifier: Modifier = Modifier,
    movie: MovieDetailUiModel,
    oneTimeEventFlow: Flow<MovieDetailEvent>
) {
    val coroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(flow = oneTimeEventFlow) { event ->
        coroutineScope.launch {
            when (event) {
                is MovieDetailEvent.OnMovieDetailFetchedError -> {
                    snackBarHostState.showSnackbar(event.error)
                }
            }
        }
    }

    CompositionLocalProvider(value = LocalSnackBar provides snackBarHostState) {
        Scaffold { paddingValues ->
            Text(
                text = "Welcome to the movie detail screen with this title: ${movie.title}!",
                color = Color.Blue,
                modifier = modifier.padding(paddingValues)
            )
        }
    }
}
