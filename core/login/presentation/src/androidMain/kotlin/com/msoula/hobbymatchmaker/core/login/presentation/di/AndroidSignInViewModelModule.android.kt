package com.msoula.hobbymatchmaker.core.login.presentation.di

import androidx.credentials.CredentialManager
import org.koin.dsl.module

actual val coreModuleSignInPlatformSpecific = module {
    single<CredentialManager> { CredentialManager.create(get()) }
}
