package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.models.SocialClientsVM
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val coreModuleSignInViewModel = module {
    viewModel { (socialClients: SocialClientsVM) ->
        SignInViewModel(
            signInInteractor = get(),
            socialClients = socialClients,
            defaultErrorMessageMapper = get()
        )
    }
}
