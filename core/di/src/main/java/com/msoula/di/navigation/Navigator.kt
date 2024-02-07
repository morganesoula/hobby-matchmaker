package com.msoula.di.navigation

import androidx.navigation.NavController

interface Navigator {
    fun setController(navController: NavController)

    fun navigate(
        route: NavigationRoute,
        shouldPopUp: Boolean = true,
    )

    fun popBackStack()

    fun clear()
}
