package com.msoula.hobbymatchmaker.core.navigation

object AppScreenRoute : NavigationRoute {
    override fun buildRoute(): String = ROUTE

    private const val ROOT = "app_screen"
    const val ROUTE = ROOT
}

object SignInScreenRoute : NavigationRoute {
    override fun buildRoute(): String = ROUTE

    private const val ROOT = "sign_in_screen"
    const val ROUTE = ROOT
}

object SignUpScreenRoute : NavigationRoute {
    override fun buildRoute(): String = ROUTE

    private const val ROOT = "sign_up_screen"
    const val ROUTE = ROOT
}

object SplashScreenRoute : NavigationRoute {
    override fun buildRoute(): String = ROUTE

    private const val ROOT = "splash_screen"
    const val ROUTE = ROOT
}
