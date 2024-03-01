package com.msoula.hobbymatchmaker.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.component.event.SwipeCardEvent
import com.msoula.movies.presentation.EmptyMovieScreen
import com.msoula.movies.presentation.MovieScreen
import com.msoula.movies.presentation.MovieStateUI

@Composable
fun HomeScreen(
    logOut: () -> Unit,
    movieStateUI: MovieStateUI,
    modifier: Modifier = Modifier,
    onSwipeLeft: (event: SwipeCardEvent) -> Unit
) {
    Scaffold(modifier = modifier) { paddingValues ->
        Surface(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (movieStateUI.movies.isEmpty()) {
                EmptyMovieScreen(errorMessage = movieStateUI.error ?: "")
            } else {
                MovieScreen(movies = movieStateUI.movies, onSwipeLeft = onSwipeLeft)
            }
        }
    }
}
