package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.design.component.PlatformBackHandler
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInScreenContent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import com.msoula.hobbymatchmaker.core.navigation.domain.SignInComponent
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SignInContent(
    component: SignInComponent,
    socialClients: Map<ProviderType, SocialUIClient>,
    facebookUIClient: FacebookUIClient
) {
    val signInViewModel = koinViewModel<SignInViewModel> {
        parametersOf(socialClients)
    }

    SignInScreenContent(
        signInViewModel = signInViewModel,
        redirectToMovieScreen = {
            component.onAuthenticated()
        },
        redirectToSignUpScreen = {
            component.onSignUpClicked()
        },
        resetSignInState = {
            signInViewModel.resetSignInState()
        },
        facebookUIClient = facebookUIClient
    )

    PlatformBackHandler()
}
