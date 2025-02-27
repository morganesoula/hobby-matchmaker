package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import org.koin.dsl.module

actual val coreModuleSignUpPlatformSpecific = module {
    single { SignUpViewModel(get(), get(), get(), get()) }
}
