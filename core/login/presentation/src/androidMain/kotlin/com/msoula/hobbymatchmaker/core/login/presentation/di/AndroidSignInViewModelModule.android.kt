package com.msoula.hobbymatchmaker.core.login.presentation.di

import androidx.credentials.CredentialManager
import com.msoula.hobbymatchmaker.core.common.AndroidStateSaver
import com.msoula.hobbymatchmaker.core.common.StateSaver
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AndroidFacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import org.koin.dsl.bind
import org.koin.dsl.module

actual val coreModuleSignInPlatformSpecific = module {
    single { AndroidStateSaver(get()) } bind StateSaver::class
    single<CredentialManager> { CredentialManager.create(get()) }
    single { AndroidFacebookUIClient(get()) } bind FacebookUIClient::class
    single { (googleUIClient: GoogleUIClient) ->
        SignInViewModel(
            authFormValidationUseCases = get(),
            stateSaver = get(),
            signInUseCase = get(),
            resetPasswordUseCase = get(),
            signInWithCredentialUseCase = get(),
            googleUIClient = googleUIClient,
            appleUIClient = null,
            facebookUIClient = get()
        )
    }
}
