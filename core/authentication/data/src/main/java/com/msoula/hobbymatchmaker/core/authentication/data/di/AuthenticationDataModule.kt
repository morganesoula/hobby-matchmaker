package com.msoula.hobbymatchmaker.core.authentication.data.di

import androidx.credentials.CredentialManager
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthenticationRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.FacebookClient
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.GoogleClient
import com.msoula.hobbymatchmaker.core.authentication.domain.dataSources.AuthenticationRemoteDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authenticationDataModule = module {
    factoryOf(::GoogleClient)
    single { CredentialManager.create(androidContext()) }
    singleOf(::FacebookClient)
    singleOf(::AuthenticationRemoteDataSourceImpl) bind AuthenticationRemoteDataSource::class
}
