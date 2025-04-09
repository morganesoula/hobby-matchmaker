package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.UnifiedSignInUseCase
import com.msoula.hobbymatchmaker.core.design.component.PlatformBackHandler
import com.msoula.hobbymatchmaker.core.di.domain.useCases.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInScreenContent
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import com.msoula.hobbymatchmaker.core.navigation.domain.SignInComponent
import org.koin.compose.koinInject

@Composable
fun SignInContent(
    component: SignInComponent,
    socialUIGoogleClient: SocialUIClient,
    socialUIFacebookClient: SocialUIClient,
    facebookUIClient: FacebookUIClient
) {
    val authFormValidationUseCase: AuthFormValidationUseCase = koinInject()
    val resetPasswordUseCase: ResetPasswordUseCase = koinInject()
    val unifiedSignInUseCase: UnifiedSignInUseCase = koinInject()

    val signInViewModel = remember {
        SignInViewModel(
            authFormValidationUseCases = authFormValidationUseCase,
            resetPasswordUseCase = resetPasswordUseCase,
            unifiedSignInUseCase = unifiedSignInUseCase,
            socialClients = mapOf(
                ProviderType.GOOGLE to socialUIGoogleClient,
                ProviderType.FACEBOOK to socialUIFacebookClient
            )
        )
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
