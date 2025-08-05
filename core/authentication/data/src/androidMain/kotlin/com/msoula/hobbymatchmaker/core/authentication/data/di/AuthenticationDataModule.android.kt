package com.msoula.hobbymatchmaker.core.authentication.data.di

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthManager
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthManagerImpl
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.FacebookAuthProvider
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.GoogleAuthProvider
import org.koin.dsl.module

actual val coreModuleAuthenticationDataPlatformSpecific = module {
    single<AuthManager> {
        AuthManagerImpl(
            listOf(
                GoogleAuthProvider(get()),
                FacebookAuthProvider(get())
            )
        )
    }
}
