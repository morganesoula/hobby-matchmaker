package com.msoula.hobbymatchmaker.core.login.presentation.sign_in

import com.msoula.hobbymatchmaker.core.navigation.AppScreenRoute
import com.msoula.hobbymatchmaker.core.navigation.Navigator
import com.msoula.hobbymatchmaker.core.navigation.SignUpScreenRoute
import com.msoula.hobbymatchmaker.core.navigation.contracts.SignInNavigation

class SignInNavigationImpl(val navigator: Navigator) : SignInNavigation {
    override fun redirectToAppScreen() = navigator.navigate(AppScreenRoute)
    override fun redirectToSignUpScreen() = navigator.navigate(SignUpScreenRoute)
}
