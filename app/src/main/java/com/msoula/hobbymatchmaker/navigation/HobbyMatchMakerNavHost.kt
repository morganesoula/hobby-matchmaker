package com.msoula.hobbymatchmaker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.msoula.auth.presentation.LoginScreen
import com.msoula.auth.presentation.LoginViewModel
import com.msoula.auth.presentation.SignUpScreen
import com.msoula.auth.presentation.SignUpViewModel
import com.msoula.di.navigation.HomeScreenRoute
import com.msoula.di.navigation.LoginScreenRoute
import com.msoula.di.navigation.Navigator
import com.msoula.di.navigation.SignUpScreenRoute
import com.msoula.hobbymatchmaker.presentation.HomeScreen
import com.msoula.hobbymatchmaker.presentation.HomeViewModel

@Composable
fun HobbyMatchMakerNavHost(
    navController: NavHostController,
    navigator: Navigator
) {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val logInViewModel = hiltViewModel<LoginViewModel>()
    val signUpViewModel = hiltViewModel<SignUpViewModel>()

    val startDestination = if (homeViewModel.checkForActiveSession()) {
        HomeScreenRoute.route
    } else {
        LoginScreenRoute.route
    }

    NavHost(navController, startDestination = startDestination) {
        composable(route = HomeScreenRoute.route) {
            HomeScreen(
                logOut = { homeViewModel.logOut() }
            )
        }

        composable(route = LoginScreenRoute.route) {
            val loginFormState by logInViewModel.loginFormState.collectAsState()
            val circularProgressLoading by logInViewModel.circularProgressLoading.collectAsState()
            val rememberedOpenResetDialog by logInViewModel.openResetDialog.collectAsState()
            val emailResetSent by logInViewModel.resettingEmailSent.collectAsState()

            LoginScreen(
                circularProgressLoading = circularProgressLoading,
                loginFormState = loginFormState,
                authUiEvent = logInViewModel::onEvent,
                redirectToSignUpScreen = { navigator.navigate(SignUpScreenRoute) },
                openResetDialog = rememberedOpenResetDialog,
                emailResetSent = emailResetSent,
                onGoogleSignInClicked = {}
            )
        }

        composable(SignUpScreenRoute.route) {
            val registrationState by signUpViewModel.registrationFormState.collectAsState()
            val signUpProgressLoading by signUpViewModel.signUpCircularProgress.collectAsState()

            SignUpScreen(
                registrationState = registrationState,
                signUpProgressLoading = signUpProgressLoading,
                authUIEvent = signUpViewModel::onEvent,
                redirectToLogInScreen = { navigator.navigate(LoginScreenRoute) }
            )
        }
    }
}