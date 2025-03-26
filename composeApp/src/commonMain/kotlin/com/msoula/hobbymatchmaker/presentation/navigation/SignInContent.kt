package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInScreenContent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.navigation.domain.SignInComponent
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SignInContent(component: SignInComponent, googleUIClient: GoogleUIClient) {
    val signInViewModel = koinViewModel<SignInViewModel> { parametersOf(googleUIClient) }

    SignInScreenContent(
        signInViewModel = signInViewModel,
        redirectToMovieScreen = {
            component.onAuthenticated()
        },
        redirectToSignUpScreen = {
            component.onSignUpClicked()
        }
    )
}
