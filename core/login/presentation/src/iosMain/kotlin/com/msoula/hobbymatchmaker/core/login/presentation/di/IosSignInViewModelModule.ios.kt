package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.IosStateSaver
import com.msoula.hobbymatchmaker.core.common.StateSaver
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AppleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AppleUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.clients.IosAppleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.IosGoogleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreModuleSignInPlatformSpecific = module {
    single { IosStateSaver() } bind StateSaver::class
    single { IosGoogleUIClient() } bind GoogleUIClient::class
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
            stateSaver = get(),
            resetPasswordUseCase = get(),
            signInWithCredentialUseCase = get(),
            googleUIClient = get(),
            appleUIClient = get(),
            facebookUIClient = null,
            unifiedSignInUseCase = get(),
            socialClients = get()
        )
    }
}
