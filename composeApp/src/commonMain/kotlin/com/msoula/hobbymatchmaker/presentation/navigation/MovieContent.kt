package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.common.asString
import com.msoula.hobbymatchmaker.core.design.component.LoadingCircularProgress
import com.msoula.hobbymatchmaker.core.design.component.PlatformBackHandler
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

    /* Box(
        Modifier
            .fillMaxSize()
            .background(Color.Red)
            .border(2.dp, Color.Yellow)
    ) {
        Text(
            "Movies placeholder",
            modifier = Modifier.align(Alignment.Center)
        )
    } */

    val moviesState by movieViewModel.movieState.collectAsState()
    val oneTimeEventChannelFlow = movieViewModel.oneTimeEventChannelFlow

    print("✅ State is: $moviesState")

    when (moviesState) {
        is MovieUiStateModel.Success -> {
            MovieScreenContent(
                movies = (moviesState as MovieUiStateModel.Success).list,
                oneTimeEventChannelFlow = oneTimeEventChannelFlow,
                redirectToMovieDetail = { id -> component.onMovieClicked(id) },
                onCardEvent = movieViewModel::onCardEvent,
                logOut = { movieViewModel.logOut() },
                redirectToAuth = { component.onLogout() }
            )
        }

        is MovieUiStateModel.Empty -> EmptyMovieScreen()
        is MovieUiStateModel.Error -> ErrorMovieScreen(error = (moviesState as MovieUiStateModel.Error).errorMessage.asString())
        else -> LoadingCircularProgress()
    }

    PlatformBackHandler()
}

