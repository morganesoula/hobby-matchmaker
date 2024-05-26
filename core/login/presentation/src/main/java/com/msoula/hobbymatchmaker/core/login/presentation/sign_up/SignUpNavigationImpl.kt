package com.msoula.hobbymatchmaker.core.login.presentation.sign_up

import com.msoula.hobbymatchmaker.core.navigation.AppScreenRoute
import com.msoula.hobbymatchmaker.core.navigation.Navigator
import com.msoula.hobbymatchmaker.core.navigation.SignInScreenRoute
import com.msoula.hobbymatchmaker.core.navigation.contracts.SignUpNavigation

class SignUpNavigationImpl(val navigator: Navigator) : SignUpNavigation {

    override fun redirectToSignInScreen() = navigator.navigate(SignInScreenRoute)
    override fun redirectToAppScreen() = navigator.navigate(AppScreenRoute)
}
