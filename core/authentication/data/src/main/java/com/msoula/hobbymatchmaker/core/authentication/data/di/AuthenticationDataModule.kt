package com.msoula.hobbymatchmaker.core.authentication.data.di

import androidx.credentials.CredentialManager
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthenticationRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.core.authentication.domain.dataSources.AuthenticationRemoteDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val authenticationDataModule = module {
    single { CredentialManager.create(androidContext()) }
    factory<AuthenticationRemoteDataSource> {
        AuthenticationRemoteDataSourceImpl(get(), get(), get())
    }
}
