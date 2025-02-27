package com.msoula.hobbymatchmaker.core.login.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module

val signInViewModelModule = module {
    includes(coreModuleSignInPlatformSpecific)
}

expect val coreModuleSignInPlatformSpecific: Module
