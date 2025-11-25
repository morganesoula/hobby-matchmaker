package com.msoula.hobbymatchmaker.core.login.presentation.di

import com.msoula.hobbymatchmaker.core.login.presentation.interactors.AuthenticationManager
import com.msoula.hobbymatchmaker.core.login.presentation.interactors.SessionManager
import com.msoula.hobbymatchmaker.core.login.presentation.interactors.SignInInteractor
import com.msoula.hobbymatchmaker.core.login.presentation.interactors.SignUpInteractor
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val coreModuleLogin = module {
    factoryOf(::AuthenticationManager)
    factoryOf(::SessionManager)
    factoryOf(::SignInInteractor)
    factoryOf(::SignUpInteractor)
}
