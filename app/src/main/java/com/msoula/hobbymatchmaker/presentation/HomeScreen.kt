package com.msoula.hobbymatchmaker.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msoula.movies.data.MovieUiStateResult
import com.msoula.movies.presentation.CardEvent
import com.msoula.movies.presentation.EmptyMovieScreen
import com.msoula.movies.presentation.ErrorMovieScreen
import com.msoula.movies.presentation.MovieScreen
import com.msoula.movies.presentation.MovieViewModel

@Composable
fun HomeScreen(
    logOut: () -> Unit,
    modifier: Modifier = Modifier,
    movieViewModel: MovieViewModel = hiltViewModel<MovieViewModel>(),
) {
    Scaffold(modifier = modifier) { paddingValues ->
        Surface(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            val viewState by movieViewModel.viewState.collectAsStateWithLifecycle()

            HomeContent(
                viewState = viewState,
                onCardEvent = movieViewModel::onCardEvent,
            )
        }
    }
}

@Composable
fun HomeContent(
    viewState: MovieUiStateResult,
    modifier: Modifier = Modifier,
    onCardEvent: (CardEvent) -> Unit,
) {
    when (viewState) {
        is MovieUiStateResult.Loading -> CircularProgressIndicator(modifier.wrapContentSize())
        is MovieUiStateResult.Empty -> EmptyMovieScreen(modifier = modifier)
        is MovieUiStateResult.Fetched -> {
            MovieScreen(
                modifier = modifier,
                movies = viewState.list,
                onCardEvent = onCardEvent,
            )
        }

        is MovieUiStateResult.Error ->
            ErrorMovieScreen(
                modifier = modifier,
                error =
                    viewState.throwable?.message
                        ?: "Invalid error exception",
            )
    }
}
