package com.msoula.hobbymatchmaker.core.authentication.data.di

import androidx.credentials.CredentialManager
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.AuthenticationRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationRemoteDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val authenticationDataModule = module {
    single { CredentialManager.create(androidContext()) }
    factory<AuthenticationRemoteDataSource> {
        AuthenticationRemoteDataSourceImpl(get(), get())
    }
}
