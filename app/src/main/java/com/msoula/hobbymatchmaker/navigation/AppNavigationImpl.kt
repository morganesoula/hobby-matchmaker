package com.msoula.hobbymatchmaker.navigation

import com.msoula.hobbymatchmaker.core.navigation.AppScreenRoute
import com.msoula.hobbymatchmaker.core.navigation.Navigator
import com.msoula.hobbymatchmaker.core.navigation.SignInScreenRoute
import com.msoula.hobbymatchmaker.core.navigation.contracts.AppNavigation

class AppNavigationImpl(private val navigator: Navigator) : AppNavigation {

    override fun navigateToAppScreen() {
        navigator.navigate(AppScreenRoute)
    }

    override fun navigateToSignInScreen() {
        navigator.navigate(SignInScreenRoute)
    }
}
