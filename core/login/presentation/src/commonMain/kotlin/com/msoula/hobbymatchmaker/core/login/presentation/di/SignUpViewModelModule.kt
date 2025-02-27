package com.msoula.hobbymatchmaker.core.login.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module

val signUpViewModelModule = module {
    includes(coreModuleSignUpPlatformSpecific)
}

expect val coreModuleSignUpPlatformSpecific: Module
