package com.msoula.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen(val route: String) {

    data object SignUpScreen : Screen(route = "sign_up_screen")
    data object LoginScreen : Screen(route = "login_screen")
    data object HomeScreen : Screen(route = "home_screen")
}

object HobbyMatchMakerAppRouter {
    var currentScreen: MutableState<Screen> = mutableStateOf(Screen.LoginScreen)

    fun navigateTo(destination: Screen) {
        currentScreen.value = destination
    }
}
