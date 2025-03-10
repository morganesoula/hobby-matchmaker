package com.msoula.hobbymatchmaker.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.msoula.hobbymatchmaker.core.design.component.LoadingCircularProgress
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInScreenContent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpScreenContent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.SplashScreenContent
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailContent
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import com.msoula.hobbymatchmaker.features.movies.presentation.EmptyMovieScreen
import com.msoula.hobbymatchmaker.features.movies.presentation.ErrorMovieScreen
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieScreenContent
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import com.msoula.hobbymatchmaker.features.movies.presentation.models.MovieUiStateModel

class SplashscreenDestination: Screen {
    @Composable
    override fun Content() {
        SplashScreenContent()
    }
}

class SignInDestination : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val signInViewModel = koinScreenModel<SignInViewModel>()
        val oneTimeEventChannelFlow = signInViewModel.oneTimeEventChannelFlow

        SignInScreenContent(
            signInViewModel = signInViewModel,
            oneTimeEventChannelFlow = oneTimeEventChannelFlow,
            redirectToMovieScreen = {
                navigator.push(MovieDestination())
            },
            redirectToSignUpScreen = {
                navigator.push(SignUpDestination())
            }
        )
    }
}

class SignUpDestination : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val signUpViewModel = koinScreenModel<SignUpViewModel>()

        SignUpScreenContent(
            redirectToSignInScreen = {
                navigator.push(SignInDestination())
            },
            redirectToMovieScreen = {
                navigator.push(MovieDestination())
            },
            signUpViewModel = signUpViewModel
        )
    }
}

class MovieDestination : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val movieViewModel = koinScreenModel<MovieViewModel>()
        val moviesState by movieViewModel.movieState.collectAsState()
        val oneTimeEventChannelFlow = movieViewModel.oneTimeEventChannelFlow

        when (moviesState) {
            is MovieUiStateModel.Success -> MovieScreenContent(
                movies = (moviesState as MovieUiStateModel.Success).list,
                oneTimeEventChannelFlow = oneTimeEventChannelFlow,
                redirectToMovieDetail = { id ->
                    navigator.push(MovieDetailDestination(id))
                },
                onCardEvent = movieViewModel::onCardEvent
            )

            is MovieUiStateModel.Empty -> EmptyMovieScreen()
            is MovieUiStateModel.Error -> ErrorMovieScreen(error = (moviesState as MovieUiStateModel.Error).errorMessage)
            else -> LoadingCircularProgress()
        }
    }
}

data class MovieDetailDestination(val movieId: Long) : Screen {
    @Composable
    override fun Content() {
        val movieDetailViewModel = koinScreenModel<MovieDetailViewModel>()

        LaunchedEffect(Unit) {
            movieDetailViewModel.loadMovieDetails(movieId)
        }

        val viewState by movieDetailViewModel.viewState.collectAsState()
        val oneTimeEventFlow = movieDetailViewModel.oneTimeEventChannelFlow

        MovieDetailContent(
            viewState = viewState,
            oneTimeEventFlow = oneTimeEventFlow,
            onPlayTrailerClicked = movieDetailViewModel::onEvent
        )
    }
}
