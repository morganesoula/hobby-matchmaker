package com.msoula.hobbymatchmaker.core.login.presentation.di

import androidx.credentials.CredentialManager
import com.msoula.hobbymatchmaker.core.login.presentation.AndroidStateSaver
import com.msoula.hobbymatchmaker.core.login.presentation.StateSaver
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AndroidFacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AndroidGoogleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val androidSignInViewModelModule = module {
    single { AndroidStateSaver(get()) } bind StateSaver::class
    single<CredentialManager> { CredentialManager.create(get()) }
    single { AndroidGoogleUIClient(get(), get()) } bind GoogleUIClient::class
    single { AndroidFacebookUIClient(get()) } bind FacebookUIClient::class
    single {
        SignInViewModel(
            authFormValidationUseCases = get(),
            stateSaver = get(),
            signInUseCase = get(),
            resetPasswordUseCase = get(),
            resourceProvider = get(),
            signInWithCredentialUseCase = get(),
            googleUIClient = get(),
            appleUIClient = null,
            facebookUIClient = get()
        )
    }
}
