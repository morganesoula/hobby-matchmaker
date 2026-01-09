package com.msoula.hobbymatchmaker.core.navigation.presentation.utils

import androidx.compose.runtime.Immutable
import androidx.navigation.NavController
import com.msoula.hobbymatchmaker.core.navigation.presentation.Auth
import com.msoula.hobbymatchmaker.core.navigation.presentation.Movies
import com.msoula.hobbymatchmaker.core.navigation.presentation.Profile
import com.msoula.hobbymatchmaker.core.navigation.presentation.Splash

@Immutable
class NavigationCallbacks(
    private val navController: NavController
) {
    val navigateAndClearToAuth: () -> Unit = {
        navController.navigate(Auth) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    val navigateToAuthFromSplash: () -> Unit = {
        navController.navigate(Auth) {
            popUpTo<Splash> { inclusive = true }
            launchSingleTop = true
        }
    }

    val navigateToMoviesFromSplash: () -> Unit = {
        navController.navigate(Movies) {
            popUpTo<Splash> { inclusive = true }
            launchSingleTop = true
        }
    }

    val navigateToMoviesFromProfile: () -> Unit = {
        navController.navigate(Movies) {
            popUpTo<Profile> { inclusive = true }
            launchSingleTop = true
        }
    }

    val navigateToMoviesFromAuth: () -> Unit = {
        navController.navigate(Movies) {
            popUpTo<Auth> { inclusive = true }
            launchSingleTop = true
        }
    }
}
