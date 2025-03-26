package com.msoula.hobbymatchmaker.core.login.presentation.di

import androidx.credentials.CredentialManager
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AndroidStateSaver
import com.msoula.hobbymatchmaker.core.common.StateSaver
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AndroidFacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreModuleSignInPlatformSpecific = module {
    single { AndroidStateSaver(get()) } bind StateSaver::class
    single<CredentialManager> { CredentialManager.create(get()) }
    single { AndroidFacebookUIClient(get()) } bind FacebookUIClient::class

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
            stateSaver = get(),
            resetPasswordUseCase = get(),
            unifiedSignInUseCase = get(),
            socialClients = get()
        )
    }
}
