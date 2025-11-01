package com.msoula.hobbymatchmaker.core.authentication.data.di

import androidx.credentials.CredentialManager
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthManager
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthManagerImpl
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.FacebookAuthProviderImpl
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.GoogleAuthProviderImpl
import org.koin.dsl.module

actual val coreModuleAuthenticationPlatformSpecificData = module {
    single<CredentialManager> { CredentialManager.create(get()) }

    single<AuthManager> {
        AuthManagerImpl(
            listOf(
                GoogleAuthProviderImpl(get()),
                FacebookAuthProviderImpl(get())
            )
        )
    }
}
