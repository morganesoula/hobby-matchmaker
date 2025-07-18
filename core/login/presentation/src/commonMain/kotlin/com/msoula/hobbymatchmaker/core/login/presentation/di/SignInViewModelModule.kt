package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.mappers.SignInErrorMessageProvider
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModuleSignInViewModel = module {
    includes(coreModuleSignInPlatformSpecific)

    single { SignInErrorMessageProvider() }

    single { (socialClients: Map<ProviderType, SocialUIClient>) ->
        SignInViewModel(
            authFormValidationUseCases = get(),
            resetPasswordUseCase = get(),
            unifiedSignInUseCase = get(),
            socialClients = socialClients,
            ioDispatcher = get(),
            errorMessageProvider = get()
        )
    }
}

expect val coreModuleSignInPlatformSpecific: Module
