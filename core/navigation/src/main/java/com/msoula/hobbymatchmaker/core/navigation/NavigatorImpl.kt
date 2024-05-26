package com.msoula.hobbymatchmaker.core.navigation

import android.util.Log
import androidx.navigation.NavController
import javax.inject.Inject

class NavigatorImpl @Inject constructor() : Navigator {
    private var navController: NavController? = null

    override fun setController(navController: NavController) {
        this.navController = navController
    }

    override fun navigate(
        route: NavigationRoute,
        shouldPopUp: Boolean
    ) {
        if (shouldPopUp) {
            val startDestination =
                if (navController?.graph?.startDestinationRoute == AppScreenRoute.ROUTE) {
                    AppScreenRoute.ROUTE
                } else {
                    SignInScreenRoute.ROUTE
                }

            navController?.navigate(route.buildRoute()) {
                popUpTo(startDestination) {
                    inclusive = true
                }
            }
        } else {
            Log.i("HMM", "Should not pop up")
            navController?.navigate(route.buildRoute())
        }
    }

    override fun popBackStack() {
        navController?.popBackStack()
    }

    override fun clear() {
        navController = null
    }
}
