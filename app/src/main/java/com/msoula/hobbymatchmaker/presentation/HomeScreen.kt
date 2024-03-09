package com.msoula.hobbymatchmaker.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.movies.data.MovieUiStateResult
import com.msoula.movies.presentation.CardEvent
import com.msoula.movies.presentation.EmptyMovieScreen
import com.msoula.movies.presentation.ErrorMovieScreen
import com.msoula.movies.presentation.MovieScreen

@Composable
fun HomeScreen(
    logOut: () -> Unit,
    movieStateUIResult: MovieUiStateResult,
    modifier: Modifier = Modifier,
    onDoubleTap: (CardEvent) -> Unit
) {
    Scaffold(modifier = modifier) { paddingValues ->
        Surface(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (movieStateUIResult) {
                is MovieUiStateResult.Loading -> CircularProgressIndicator()
                is MovieUiStateResult.Empty -> EmptyMovieScreen()
                is MovieUiStateResult.Fetched -> MovieScreen(
                    movies = movieStateUIResult.list,
                    onDoubleTap = onDoubleTap
                )

                is MovieUiStateResult.Error -> ErrorMovieScreen(
                    error = movieStateUIResult.throwable?.message ?: "Invalid error exception"
                )
            }
        }
    }
}
