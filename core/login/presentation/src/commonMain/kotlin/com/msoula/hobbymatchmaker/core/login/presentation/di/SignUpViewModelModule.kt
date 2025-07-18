package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import com.msoula.hobbymatchmaker.core.login.presentation.signUp.mappers.SignUpErrorMessageProvider
import org.koin.dsl.module

val coreModuleSignUpViewModel = module {
    single { SignUpErrorMessageProvider() }
    single { SignUpViewModel(get(), get(), get(), get()) }
}
