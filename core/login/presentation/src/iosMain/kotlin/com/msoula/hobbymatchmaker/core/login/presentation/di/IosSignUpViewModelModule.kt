package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import org.koin.dsl.module

val iosSignUpViewModelModule = module {
    single { SignUpViewModel(get(), get(), get(), get()) }
}
