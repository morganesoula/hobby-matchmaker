package com.msoula.hobbymatchmaker.core.splashscreen.presentation

import com.msoula.hobbymatchmaker.core.navigation.AppScreenRoute
import com.msoula.hobbymatchmaker.core.navigation.Navigator
import com.msoula.hobbymatchmaker.core.navigation.SignInScreenRoute
import com.msoula.hobbymatchmaker.core.navigation.contracts.SplashScreenNavigation

class SplashScreenNavigationImpl(private val navigator: Navigator) : SplashScreenNavigation {
    override fun navigateToSignInScreen() = navigator.navigate(SignInScreenRoute, true)
    override fun navigateToAppScreen() = navigator.navigate(AppScreenRoute, true)
}
