package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SignInViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val signInViewModelModule = module {
    singleOf(::SignInViewModel)
}
