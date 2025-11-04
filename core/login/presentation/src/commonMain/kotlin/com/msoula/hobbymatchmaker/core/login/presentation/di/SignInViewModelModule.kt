package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import org.koin.dsl.module

val coreModuleSignInViewModel = module {
    single { (socialClients: Map<ProviderType, SocialUIClient>) ->
        SignInViewModel(
            authFormValidationUseCases = get(),
            resetPasswordUseCase = get(),
            observeShouldShowGuestDialog = get(),
            setShouldShowGuestDialogUseCase = get(),
            setCurrentUserProfileUuidUseCase = get(),
            unifiedSignInUseCase = get(),
            socialClients = socialClients,
            defaultErrorMessageMapper = get()
        )
    }
}
