package com.msoula.hobbymatchmaker.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.credentials.GetCredentialResponse
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.AuthCredential
import com.msoula.hobbymatchmaker.core.common.AuthUiStateModel
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.GoogleAuthClient
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.SignInScreen
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.sign_up.SignUpScreen
import com.msoula.hobbymatchmaker.core.login.presentation.sign_up.SignUpViewModel
import com.msoula.hobbymatchmaker.core.navigation.AppScreenRoute
import com.msoula.hobbymatchmaker.core.navigation.SignInScreenRoute
import com.msoula.hobbymatchmaker.core.navigation.SignUpScreenRoute
import com.msoula.hobbymatchmaker.core.navigation.SplashScreenRoute
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.SplashScreen
import com.msoula.hobbymatchmaker.presentation.AppScreen
import com.msoula.hobbymatchmaker.presentation.AppViewModel

@SuppressLint("ComposeModifierMissing", "ComposeViewModelInjection")
@Stable
@Composable
fun HobbyMatchMakerNavHost(
    navController: NavHostController,
    appViewModel: AppViewModel,
    googleAuthClient: GoogleAuthClient
) {
    val authenticationState by appViewModel.authenticationState.collectAsStateWithLifecycle()

    val startDestination = when (authenticationState) {
        is AuthUiStateModel.CheckingState -> SplashScreenRoute.ROUTE
        is AuthUiStateModel.NotConnected -> SignInScreenRoute.ROUTE
        is AuthUiStateModel.IsConnected -> AppScreenRoute.ROUTE
    }

    NavHost(navController, startDestination = startDestination) {
        composable(route = AppScreenRoute.ROUTE) {
            AppScreen(appViewModel = appViewModel)
        }

        composable(route = SignInScreenRoute.ROUTE) {
            val signInViewModel: SignInViewModel = hiltViewModel<SignInViewModel>()

            SignInScreen(
                redirectToSignUpScreen = signInViewModel::redirectToSignUpScreen,
                signInViewModel = signInViewModel,
                handleFacebookAccessToken = { credential: AuthCredential ->
                    signInViewModel.handleFacebookLogin(
                        credential
                    )
                },
                handleGoogleSignIn = { result: GetCredentialResponse?, googleAuthClient: GoogleAuthClient ->
                    signInViewModel.handleGoogleLogin(result, googleAuthClient)
                },
                googleAuthClient = googleAuthClient
            )
        }

        composable(SignUpScreenRoute.ROUTE) {
            val signUpViewModel: SignUpViewModel = hiltViewModel<SignUpViewModel>()

            SignUpScreen(
                redirectToLogInScreen = signUpViewModel::redirectToSignInScreen
            )
        }

        composable(SplashScreenRoute.ROUTE) {
            SplashScreen()
        }
    }
}
