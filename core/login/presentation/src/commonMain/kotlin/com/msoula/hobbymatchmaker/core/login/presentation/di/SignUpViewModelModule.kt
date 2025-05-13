package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import org.koin.dsl.module

val coreModuleSignUpViewModel = module {
    single { SignUpViewModel(get(), get()) }
}
