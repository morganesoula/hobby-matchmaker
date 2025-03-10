package com.msoula.hobbymatchmaker.core.login.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module

val coreModuleSignUpViewModel = module {
    includes(coreModuleSignUpPlatformSpecific)
}

expect val coreModuleSignUpPlatformSpecific: Module
