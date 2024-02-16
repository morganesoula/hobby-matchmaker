package com.msoula.di.navigation

object HomeScreenRoute : NavigationRoute {
    override fun buildRoute(): String = ROUTE

    private const val ROOT = "home_screen"
    const val ROUTE = ROOT
}

object LoginScreenRoute : NavigationRoute {
    override fun buildRoute(): String = ROUTE

    private const val ROOT = "login_screen"
    const val ROUTE = ROOT
}

object SignUpScreenRoute : NavigationRoute {
    override fun buildRoute(): String = ROUTE

    private const val ROOT = "sign_up_screen"
    const val ROUTE = ROOT
}
