package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.login.presentation.orchestrators.AuthenticationManager
import com.msoula.hobbymatchmaker.core.login.presentation.orchestrators.SessionManager
import com.msoula.hobbymatchmaker.core.login.presentation.orchestrators.SignInOrchestrator
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val coreModuleLogin = module {
    factoryOf(::AuthenticationManager)
    factoryOf(::SessionManager)
    factoryOf(::SignInOrchestrator)
}
