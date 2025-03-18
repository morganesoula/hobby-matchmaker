package com.msoula.hobbymatchmaker.core.authentication.data.di

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthenticationRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.core.authentication.domain.dataSources.AuthenticationRemoteDataSource
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleAuthenticationData = module {
    single { Firebase.auth }
    single { Firebase.firestore }

    includes(coreModuleAuthenticationDataPlatformSpecific)
    singleOf(::AuthenticationRemoteDataSourceImpl) bind AuthenticationRemoteDataSource::class
}

expect val coreModuleAuthenticationDataPlatformSpecific: Module
