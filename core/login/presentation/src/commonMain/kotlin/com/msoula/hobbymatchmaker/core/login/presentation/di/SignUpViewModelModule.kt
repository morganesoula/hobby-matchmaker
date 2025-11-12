package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val coreModuleSignUpViewModel = module {
    viewModel {
        SignUpViewModel(
            loginValidateFormUseCase = get(),
            signUpUseCase = get(),
            defaultErrorMessageMapper = get()
        )
    }
}
