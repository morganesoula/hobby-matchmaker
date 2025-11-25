package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val coreModuleSignInViewModel = module {
    viewModel { (socialClients: Map<ProviderType, SocialUIClient>) ->
        SignInViewModel(
            signInInteractor = get(),
            socialClients = socialClients,
            defaultErrorMessageMapper = get()
        )
    }
}
