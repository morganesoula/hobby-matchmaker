package com.msoula.hobbymatchmaker.core.login.presentation.di

import androidx.credentials.CredentialManager
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreModuleSignInPlatformSpecific = module {
    single<CredentialManager> { CredentialManager.create(get()) }

    single { GoogleUIClientImpl(get()) } bind SocialUIClient::class
    single { FacebookUIClientImpl(get()) } bind SocialUIClient::class

    single<Map<ProviderType, SocialUIClient>> {
        mapOf(
            ProviderType.GOOGLE to get<GoogleUIClientImpl>(),
            ProviderType.FACEBOOK to get<FacebookUIClientImpl>()
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
