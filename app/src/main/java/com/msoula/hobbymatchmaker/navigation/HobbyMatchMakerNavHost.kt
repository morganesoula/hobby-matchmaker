package com.msoula.hobbymatchmaker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.msoula.auth.presentation.GoogleAuthUIClient
import com.msoula.auth.presentation.LoginScreen
import com.msoula.auth.presentation.SignUpScreen
import com.msoula.di.navigation.HomeScreenRoute
import com.msoula.di.navigation.LoginScreenRoute
import com.msoula.di.navigation.Navigator
import com.msoula.di.navigation.SignUpScreenRoute
import com.msoula.hobbymatchmaker.presentation.HomeScreen
import com.msoula.hobbymatchmaker.presentation.HomeViewModel

@Stable
@Composable
fun HobbyMatchMakerNavHost(
    navController: NavHostController,
    navigator: Navigator,
    homeViewModel: HomeViewModel = hiltViewModel(),
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
            HomeScreen(
                logOut = { homeViewModel.logOut() },
            )
        }

        composable(route = LoginScreenRoute.ROUTE) {
            LoginScreen(
                redirectToHomeScreen = { navigator.navigate(HomeScreenRoute) },
                redirectToSignUpScreen = { navigator.navigate(SignUpScreenRoute) },
                googleAuthUIClient = googleAuthUiClient,
            )
        }

        composable(SignUpScreenRoute.ROUTE) {
            SignUpScreen(
                redirectToLogInScreen = { navigator.navigate(LoginScreenRoute) },
            )
        }
    }
}
