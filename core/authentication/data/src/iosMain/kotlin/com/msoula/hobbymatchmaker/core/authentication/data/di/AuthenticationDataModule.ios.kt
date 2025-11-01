package com.msoula.hobbymatchmaker.core.authentication.data.di

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AppleAuthProviderImpl
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthManager
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthManagerImpl
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.GoogleAuthProviderImpl
import org.koin.dsl.module

actual val coreModuleAuthenticationPlatformSpecificData = module {
    single<AuthManager> {
        AuthManagerImpl(
            listOf(
                GoogleAuthProviderImpl(get()),
                AppleAuthProviderImpl(get())
            )
        )
    }
}
