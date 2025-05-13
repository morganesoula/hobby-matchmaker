package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.msoula.hobbymatchmaker.core.navigation.domain.MovieDetailComponent
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailContent
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MovieDetailContent(component: MovieDetailComponent) {
    val movieDetailViewModel =
        koinViewModel<MovieDetailViewModel>(key = "movie_detail_${component.movieId}") {
            parametersOf(
                component.movieId
            )
        }

    val viewState by movieDetailViewModel.viewState.collectAsState()
    val oneTimeEventFlow = movieDetailViewModel.oneTimeEventChannelFlow

    MovieDetailContent(
        viewState = viewState,
        oneTimeEventFlow = oneTimeEventFlow,
        onPlayTrailerClicked = movieDetailViewModel::onEvent,
        onMovieDetailBackPressed = component.onMovieDetailBackPressed()
    )
}
