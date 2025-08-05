package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.mappers.SignUpErrorMessageProvider
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleSignUpViewModel = module {
    single(named("signUpErrorMessageProvider")) {
        SignUpErrorMessageProvider()
    } bind ErrorMessageProvider::class

    single {
        SignUpViewModel(
            loginValidateFormUseCase = get(),
            signUpUseCase = get(),
            errorMessageProvider = get(named("signUpErrorMessageProvider")),
            ioDispatcher = get()
        )
    }
}
