package com.msoula.hobbymatchmaker.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.msoula.hobbymatchmaker.core.common.AuthUiStateModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInScreen
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpScreen
import com.msoula.hobbymatchmaker.core.navigation.contracts.Destinations
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.SplashScreen
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.MovieDetailContent
import com.msoula.hobbymatchmaker.feature.moviedetail.presentation.MovieDetailViewModel
import com.msoula.hobbymatchmaker.presentation.AppScreen
import com.msoula.hobbymatchmaker.presentation.AppViewModel
import org.koin.androidx.compose.koinViewModel

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@SuppressLint("ComposeModifierMissing", "ComposeViewModelInjection", "RestrictedApi")
@Stable
@Composable
fun HobbyMatchMakerNavigationRoot(
    navController: NavHostController,
    appViewModel: AppViewModel
) {
    val authenticationState by appViewModel.authenticationState.collectAsStateWithLifecycle()

    val startDestination = when (authenticationState) {
        is AuthUiStateModel.IsConnected -> Destinations.Main
        is AuthUiStateModel.NotConnected -> Destinations.Auth
        else -> Destinations.Splash
    }

    NavHost(navController, startDestination = startDestination) {
        splashGraph()
        authGraph(navController)
        mainGraph(navController, appViewModel)
    }
}

@SuppressLint("RestrictedApi")
private fun NavGraphBuilder.authGraph(
    navController: NavHostController
) {
    navigation<Destinations.Auth>(
        startDestination = Destinations.Auth.SignIn,
    ) {
        composable<Destinations.Auth.SignIn> {
            val signInViewModel: SignInViewModel = koinViewModel<SignInViewModel>()

            SignInScreen(
                redirectToSignUpScreen = { navController.navigate(Destinations.Auth.SignUp) },
                signInViewModel = signInViewModel,
                redirectToAppScreen = {
                    navController.navigate(Destinations.Main.App) {
                        popUpTo(Destinations.Main.App)
                    }
                },
                oneTimeEventChannelFlow = signInViewModel.oneTimeEventChannelFlow,
                connectWithSocialMedia = { facebookAccessToken, context ->
                    signInViewModel.connectWithSocialMedia(facebookAccessToken, context)
                }
            )
        }

        composable<Destinations.Auth.SignUp> {
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
                }
            )
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
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
                redirectToLoginScreen = {
                    navController.navigate(
                        Destinations.Auth.SignIn
                    )
                },
                redirectToMovieDetail = { movieId: Long ->
                    navController.navigate(
                        Destinations.Main.MovieDetail(
                            movieId
                        )
                    )
                })
        }

        composable<Destinations.Main.MovieDetail> {
            val movieDetailViewModel: MovieDetailViewModel = koinViewModel<MovieDetailViewModel>()
            val viewStateBis by movieDetailViewModel.viewState.collectAsStateWithLifecycle()

            MovieDetailContent(
                viewState = viewStateBis,
                oneTimeEventFlow = movieDetailViewModel.oneTimeEventChannelFlow,
                onPlayTrailerClicked = movieDetailViewModel::onEvent
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


