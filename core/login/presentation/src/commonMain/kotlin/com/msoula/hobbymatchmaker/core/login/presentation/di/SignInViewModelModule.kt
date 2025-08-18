package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.mappers.SignInErrorMessageProvider
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleSignInViewModel = module {
    includes(coreModuleSignInPlatformSpecific)

    single(named("signInErrorMessageProvider")) {
        SignInErrorMessageProvider()
    } bind ErrorMessageProvider::class

    single { (socialClients: Map<ProviderType, SocialUIClient>) ->
        SignInViewModel(
            authFormValidationUseCases = get(),
            resetPasswordUseCase = get(),
            observeShouldShowGuestDialog = get(),
            setShouldShowGuestDialogUseCase = get(),
            unifiedSignInUseCase = get(),
            socialClients = socialClients,
            ioDispatcher = get(),
            errorMessageProvider = get(named("signInErrorMessageProvider"))
        )
    }
}

expect val coreModuleSignInPlatformSpecific: Module
