package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AppleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AppleUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.clients.IosAppleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreModuleSignInPlatformSpecific = module {
    //TODO What about IosGoogleUIClient?
    single { IosAppleUIClient() } bind AppleUIClient::class

    single { GoogleUIClientImpl(get()) } bind SocialUIClient::class
    single { AppleUIClientImpl(get()) } bind SocialUIClient::class

    single<Map<ProviderType, SocialUIClient>> {
        mapOf(
            ProviderType.GOOGLE to get<GoogleUIClientImpl>(),
            ProviderType.APPLE to get<AppleUIClientImpl>()
        )
    }

    single {
        SignInViewModel(
            authFormValidationUseCases = get(),
            resetPasswordUseCase = get(),
            unifiedSignInUseCase = get(),
            socialClients = get()
        )
    }
}
