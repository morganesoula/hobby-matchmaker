package com.msoula.hobbymatchmaker.core.navigation.presentation.utils

import androidx.compose.runtime.Immutable
import androidx.navigation.NavController
import com.msoula.hobbymatchmaker.core.navigation.presentation.Auth
import com.msoula.hobbymatchmaker.core.navigation.presentation.Hub
import com.msoula.hobbymatchmaker.core.navigation.presentation.Main
import com.msoula.hobbymatchmaker.core.navigation.presentation.MovieDetail
import com.msoula.hobbymatchmaker.core.navigation.presentation.Profile
import com.msoula.hobbymatchmaker.core.navigation.presentation.Social
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
        navController.navigate(Main) {
            popUpTo<Splash> { inclusive = true }
            launchSingleTop = true
        }
    }

    val navigateToMoviesFromProfile: () -> Unit = {
        navController.navigate(Main) {
            popUpTo<Profile> { inclusive = true }
            launchSingleTop = true
        }
    }

    val navigateToMoviesFromAuth: () -> Unit = {
        navController.navigate(Main) {
            popUpTo<Auth> { inclusive = true }
            launchSingleTop = true
        }
    }

    val navigateToProfileFromMovies: () -> Unit = {
        navController.navigate(Profile) {
            launchSingleTop = true
        }
    }

    val navigateToSocialFromMovies: () -> Unit = {
        navController.navigate(Social) {
            launchSingleTop = true
        }
    }

    val navigateToMoviesFromHub: () -> Unit = {
        navController.navigate(Main) {
            popUpTo<Hub> { inclusive = true }
            launchSingleTop = true
        }
    }

    val navigateToProfileFromHub: () -> Unit = {
        navController.navigate(Profile) {
            popUpTo<Hub> { inclusive = true }
            launchSingleTop = true
        }
    }

    fun navigateToMovieDetailFromMoviesOrHub(movieId: Long) =
        navController.navigate(MovieDetail(movieId)) {
            launchSingleTop = true
        }
}
