package com.msoula.hobbymatchmaker.navigation

import android.app.Activity.RESULT_OK
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.msoula.auth.presentation.GoogleAuthUIClient
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
import com.msoula.movies.presentation.MovieViewModel
import kotlinx.coroutines.launch

@Stable
@Composable
fun ComponentActivity.HobbyMatchMakerNavHost(
    navController: NavHostController,
    navigator: Navigator,
    homeViewModel: HomeViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    movieViewModel: MovieViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val startDestination =
        if (homeViewModel.checkForActiveSession()) {
            HomeScreenRoute.ROUTE
        } else {
            LoginScreenRoute.ROUTE
        }

    val googleAuthUiClient by lazy {
        GoogleAuthUIClient(
            context,
            FirebaseAuth.getInstance(),
            Identity.getSignInClient(context),
        )
    }

    NavHost(navController, startDestination = startDestination) {
        composable(route = HomeScreenRoute.ROUTE) {
            val moveStateUIResult = movieViewModel.viewState.collectAsStateWithLifecycle()

            HomeScreen(
                movieStateUIResult = moveStateUIResult.value,
                logOut = { homeViewModel.logOut() },
                onDoubleTap = movieViewModel::onCardEvent
            )
        }

        composable(route = LoginScreenRoute.ROUTE) {
            val loginFormState by loginViewModel.formDataFlow.collectAsStateWithLifecycle()
            val circularProgressLoading by loginViewModel.circularProgressLoading.collectAsStateWithLifecycle()
            val rememberedOpenResetDialog by loginViewModel.openResetDialog.collectAsStateWithLifecycle()
            val emailResetSent by loginViewModel.resettingEmailSent.collectAsStateWithLifecycle()

            val launcher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                ) { result ->
                    if (result.resultCode == RESULT_OK) {
                        lifecycleScope.launch {
                            val signInResult =
                                googleAuthUiClient.signInWithIntent(
                                    intent = result.data ?: return@launch,
                                )

                            loginViewModel.onGoogleSignInEvent(signInResult)
                        }
                    }
                }

            LoginScreen(
                circularProgressLoading = circularProgressLoading,
                loginFormState = loginFormState,
                authUiEvent = loginViewModel::onEvent,
                redirectToHomeScreen = { navigator.navigate(HomeScreenRoute) },
                redirectToSignUpScreen = { navigator.navigate(SignUpScreenRoute) },
                openResetDialog = rememberedOpenResetDialog,
                emailResetSent = emailResetSent,
                onGoogleSignInClicked = {
                    lifecycleScope.launch {
                        val signInIntentSender = googleAuthUiClient.signInWithGoogle()

                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch,
                            ).build(),
                        )
                    }
                },
            )
        }

        composable(SignUpScreenRoute.ROUTE) {
            val registrationState by signUpViewModel.formDataFlow.collectAsStateWithLifecycle()
            val signUpProgressLoading by signUpViewModel.signUpCircularProgress.collectAsStateWithLifecycle()

            SignUpScreen(
                registrationState = registrationState,
                signUpProgressLoading = signUpProgressLoading,
                authUIEvent = signUpViewModel::onEvent,
                redirectToLogInScreen = { navigator.navigate(LoginScreenRoute) },
            )
        }
    }
}
