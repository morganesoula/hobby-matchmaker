package com.msoula.di.navigation

object HomeScreenRoute : NavigationRoute {
    override fun buildRoute(): String = route

    private const val root = "home_screen"
    const val route = root
}

object LoginScreenRoute : NavigationRoute {
    override fun buildRoute(): String = route

    private const val root = "login_screen"
    const val route = root
}

object SignUpScreenRoute : NavigationRoute {
    override fun buildRoute(): String = route

    private const val root = "sign_up_screen"
    const val route = root
}

