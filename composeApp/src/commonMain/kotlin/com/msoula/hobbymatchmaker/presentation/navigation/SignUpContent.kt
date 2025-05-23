package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpScreenContent
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import com.msoula.hobbymatchmaker.core.navigation.domain.SignUpComponent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignUpContent(component: SignUpComponent) {
    val signUpViewModel = koinViewModel<SignUpViewModel>()

    SignUpScreenContent(
        redirectToSignInScreen = {
            component.onSignInClicked()
        },
        redirectToMovieScreen = {
            component.onAuthenticated()
        },
        signUpViewModel = signUpViewModel
    )
}
