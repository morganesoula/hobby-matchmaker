package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.msoula.hobbymatchmaker.core.design.component.LoadingCircularProgress
import com.msoula.hobbymatchmaker.core.navigation.domain.MainComponent
import com.msoula.hobbymatchmaker.features.movies.presentation.EmptyMovieScreen
import com.msoula.hobbymatchmaker.features.movies.presentation.ErrorMovieScreen
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieScreenContent
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MovieContent(component: MainComponent) {
    val movieViewModel = koinViewModel<MovieViewModel>()
    val moviesState by movieViewModel.movieState.collectAsState()
    val oneTimeEventChannelFlow = movieViewModel.oneTimeEventChannelFlow

    when (moviesState) {
        is MovieUiStateModel.Success -> MovieScreenContent(
            movies = (moviesState as MovieUiStateModel.Success).list,
            oneTimeEventChannelFlow = oneTimeEventChannelFlow,
            redirectToMovieDetail = { id -> component.onMovieClicked(id) },
            onCardEvent = movieViewModel::onCardEvent
        )

        is MovieUiStateModel.Empty -> EmptyMovieScreen()
        is MovieUiStateModel.Error -> ErrorMovieScreen(error = (moviesState as MovieUiStateModel.Error).errorMessage)
        else -> LoadingCircularProgress()
    }
}
