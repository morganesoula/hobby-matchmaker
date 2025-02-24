package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.login.presentation.signUp.SignUpViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val signUpViewModelModule = module {
    singleOf(::SignUpViewModel)
}
