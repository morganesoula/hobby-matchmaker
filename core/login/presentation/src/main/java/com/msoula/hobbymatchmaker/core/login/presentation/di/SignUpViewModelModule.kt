package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.login.presentation.sign_up.SignUpViewModel
import org.koin.dsl.module

val signUpViewModelModule = module {
    single<SignUpViewModel> {
        SignUpViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}
