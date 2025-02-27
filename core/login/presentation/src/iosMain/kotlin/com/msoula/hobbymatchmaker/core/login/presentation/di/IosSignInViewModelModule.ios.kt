package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.common.IosStateSaver
import com.msoula.hobbymatchmaker.core.common.StateSaver
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AppleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.IosAppleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.IosGoogleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreModuleSignInPlatformSpecific = module {
    single { IosStateSaver() } bind StateSaver::class
    single { IosGoogleUIClient() } bind GoogleUIClient::class
    single { IosAppleUIClient() } bind AppleUIClient::class
    single {
        SignInViewModel(
            authFormValidationUseCases = get(),
            stateSaver = get(),
            signInUseCase = get(),
            resetPasswordUseCase = get(),
            signInWithCredentialUseCase = get(),
            googleUIClient = get(),
            appleUIClient = get(),
            facebookUIClient = null
        )
    }
}
