package com.msoula.hobbymatchmaker.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.credentials.GetCredentialResponse
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.firebase.auth.AuthCredential
import com.msoula.hobbymatchmaker.core.common.AuthUiStateModel
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.GoogleAuthClient
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.SignInScreen
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.sign_up.SignUpScreen
import com.msoula.hobbymatchmaker.core.login.presentation.sign_up.SignUpViewModel
import com.msoula.hobbymatchmaker.core.navigation.contracts.Destinations
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.SplashScreen
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.MovieDetailContent
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.MovieDetailViewModel
import com.msoula.hobbymatchmaker.presentation.AppScreen
import com.msoula.hobbymatchmaker.presentation.AppViewModel

@SuppressLint("ComposeModifierMissing", "ComposeViewModelInjection")
@Stable
@Composable
fun HobbyMatchMakerNavigationRoot(
    navController: NavHostController,
    appViewModel: AppViewModel,
    googleAuthClient: GoogleAuthClient
) {
    val authenticationState by appViewModel.authenticationState.collectAsStateWithLifecycle()

    val startDestination = when (authenticationState) {
        is AuthUiStateModel.IsConnected -> Destinations.Main
        is AuthUiStateModel.NotConnected -> Destinations.Auth
        else -> Destinations.Splash
    }

    NavHost(navController, startDestination = startDestination) {
        splashGraph()
        authGraph(navController, googleAuthClient)
        mainGraph(navController, appViewModel)
    }
}

private fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    googleAuthClient: GoogleAuthClient
) {
    navigation<Destinations.Auth>(
        startDestination = Destinations.Auth.SignIn,
    ) {
        composable<Destinations.Auth.SignIn> {
            val signInViewModel: SignInViewModel = hiltViewModel<SignInViewModel>()

            SignInScreen(
                redirectToSignUpScreen = { navController.navigate(Destinations.Auth.SignUp) },
                signInViewModel = signInViewModel,
                handleFacebookAccessToken = { credential: AuthCredential ->
                    signInViewModel.handleFacebookLogin(
                        credential
                    )
                },
                handleGoogleSignIn = { result: GetCredentialResponse?, googleAuthClient: GoogleAuthClient ->
                    signInViewModel.handleGoogleLogin(result, googleAuthClient)
                },
                googleAuthClient = googleAuthClient,
                redirectToAppScreen = {
                    navController.navigate(Destinations.Main.App) {
                        popUpTo(Destinations.Main.App)
                    }
                },
                oneTimeEventChannelFlow = signInViewModel.oneTimeEventChannelFlow
            )
        }

        composable<Destinations.Auth.SignUp> {
            val signUpViewModel: SignUpViewModel = hiltViewModel<SignUpViewModel>()

            SignUpScreen(
                redirectToLogInScreen = {
                    navController.navigate(Destinations.Auth.SignIn) {
                        popUpTo(Destinations.Auth.SignUp) {
                            inclusive = true
                            saveState = true
                        }

                        restoreState = true
                    }
                },
                redirectToAppScreen = {
                    navController.navigate(Destinations.Main.App)
                },
                oneTimeEventChannelFlow = signUpViewModel.oneTimeEventChannelFlow
            )
        }
    }
}

private fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    appViewModel: AppViewModel
) {
    navigation<Destinations.Main>(
        startDestination = Destinations.Main.App
    ) {
        composable<Destinations.Main.App> {
            AppScreen(
                appViewModel = appViewModel,
                redirectToMovieDetail = { movieId: Long ->
                    navController.navigate(
                        Destinations.Main.MovieDetail(
                            movieId
                        )
                    )
                })
        }

        composable<Destinations.Main.MovieDetail> {
            val movieDetailViewModel: MovieDetailViewModel = hiltViewModel<MovieDetailViewModel>()
            val viewState by movieDetailViewModel.viewState.collectAsStateWithLifecycle()

            MovieDetailContent(
                viewState = viewState,
                oneTimeEventFlow = movieDetailViewModel.oneTimeEventChannelFlow
            )
        }
    }
}

private fun NavGraphBuilder.splashGraph() {
    navigation<Destinations.Splash>(
        startDestination = Destinations.Splash.SplashScreen
    ) {
        composable<Destinations.Splash.SplashScreen> {
            SplashScreen()
        }
    }
}


