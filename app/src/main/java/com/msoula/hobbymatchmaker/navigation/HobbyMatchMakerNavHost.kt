package com.msoula.hobbymatchmaker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.msoula.auth.data.AuthUIEvent
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
fun HobbyMatchMakerNavHost(navController: NavHostController, navigator: Navigator) {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val signUpViewModel = hiltViewModel<SignUpViewModel>()
    val logInViewModel = hiltViewModel<LoginViewModel>()

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
            val circularProgressLoading = logInViewModel.circularProgressLoading.value
            val loginFormState = logInViewModel.logInState.collectAsState().value
            val rememberedOpenResetDialog = logInViewModel.openResetDialog.value
            val emailResetSent = logInViewModel.resettingEmailSent.value

            LoginScreen(
                circularProgressLoading = circularProgressLoading,
                loginFormState = loginFormState,
                onEmailChanged = { email -> logInViewModel.onEvent(AuthUIEvent.OnEmailChanged(email)) },
                onPasswordChanged = { password ->
                    logInViewModel.onEvent(
                        AuthUIEvent.OnPasswordChanged(
                            password
                        )
                    )
                },
                onForgotPasswordClicked = { logInViewModel.onEvent(AuthUIEvent.OnForgotPasswordClicked) },
                onHideForgotPasswordDialog = { logInViewModel.onEvent(AuthUIEvent.HideForgotPasswordDialog) },
                onLogIn = { logInViewModel.onEvent(AuthUIEvent.OnLogIn) },
                redirectToSignUpScreen = { navigator.navigate(SignUpScreenRoute) },
                onResetPasswordConfirmed = { logInViewModel.onEvent(AuthUIEvent.OnResetPasswordConfirmed) },
                openResetDialog = rememberedOpenResetDialog,
                onEmailResetChanged = { emailReset ->
                    logInViewModel.onEvent(
                        AuthUIEvent.OnEmailResetChanged(
                            emailReset
                        )
                    )
                },
                emailResetSent = emailResetSent
            )
        }

        composable(SignUpScreenRoute.route) {
            val registrationState by signUpViewModel.registrationFormState.collectAsState()
            val signUpProgressLoading by signUpViewModel.signUpCircularProgress.collectAsState()

            SignUpScreen(
                registrationState = registrationState,
                signUpProgressLoading = signUpProgressLoading,
                onEmailChanged = { email -> signUpViewModel.onEvent(AuthUIEvent.OnEmailChanged(email)) },
                onPasswordChanged = { password ->
                    signUpViewModel.onEvent(
                        AuthUIEvent.OnPasswordChanged(
                            password
                        )
                    )
                },
                onFirstNameChanged = { firstName ->
                    signUpViewModel.onEvent(
                        AuthUIEvent.OnFirstNameChanged(
                            firstName
                        )
                    )
                },
                onLastNameChanged = { lastName ->
                    signUpViewModel.onEvent(
                        AuthUIEvent.OnLastNameChanged(
                            lastName
                        )
                    )
                },
                onSignUp = { signUpViewModel.onEvent(AuthUIEvent.OnSignUp) },
                redirectToLogInScreen = { navigator.navigate(LoginScreenRoute) }
            )
        }
    }
}