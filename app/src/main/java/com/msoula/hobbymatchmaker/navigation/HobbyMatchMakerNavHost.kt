package com.msoula.hobbymatchmaker.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.msoula.hobbymatchmaker.core.common.AuthUiStateModel
import com.msoula.hobbymatchmaker.core.di.navigation.AppScreenRoute
import com.msoula.hobbymatchmaker.core.di.navigation.Navigator
import com.msoula.hobbymatchmaker.core.di.navigation.SignInScreenRoute
import com.msoula.hobbymatchmaker.core.di.navigation.SignUpScreenRoute
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.SignInScreen
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.utils.GoogleAuthUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.sign_up.SignUpScreen
import com.msoula.hobbymatchmaker.presentation.AppScreen
import com.msoula.hobbymatchmaker.presentation.AppViewModel

@SuppressLint("ComposeModifierMissing")
@Stable
@Composable
fun HobbyMatchMakerNavHost(
    navController: NavHostController,
    navigator: Navigator,
    appViewModel: AppViewModel
) {
    val context = LocalContext.current
    val authState by appViewModel.authState.collectAsStateWithLifecycle()

    when (authState) {
        is AuthUiStateModel.CheckingState -> CircularProgressIndicator()
        is AuthUiStateModel.NotConnected -> navigator.navigate(SignInScreenRoute)
        is AuthUiStateModel.IsConnected -> navigator.navigate(AppScreenRoute)
    }

    val googleAuthUiClient by lazy {
        GoogleAuthUIClient(
            context,
            FirebaseAuth.getInstance(),
            Identity.getSignInClient(context)
        )
    }

    NavHost(navController, startDestination = AppScreenRoute.ROUTE) {
        composable(route = AppScreenRoute.ROUTE) {
            AppScreen(appViewModel = appViewModel)
        }

        composable(route = SignInScreenRoute.ROUTE) {
            SignInScreen(
                redirectToHomeScreen = { navigator.navigate(AppScreenRoute) },
                redirectToSignUpScreen = { navigator.navigate(SignUpScreenRoute) },
                googleAuthUIClient = googleAuthUiClient
            )
        }

        composable(SignUpScreenRoute.ROUTE) {
            SignUpScreen(
                redirectToLogInScreen = { navigator.navigate(SignInScreenRoute) }
            )
        }
    }
}
