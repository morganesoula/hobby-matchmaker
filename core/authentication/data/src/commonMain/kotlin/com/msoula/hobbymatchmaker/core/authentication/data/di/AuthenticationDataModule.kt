package com.msoula.hobbymatchmaker.core.authentication.data.di

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthenticationRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.core.authentication.data.repositories.AuthenticationRepositoryImpl
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleAuthenticationData = module {
    includes(coreModuleAuthenticationPlatformSpecificData)
    single { Firebase.auth }
    single { Firebase.firestore }
    single { Firebase.storage("gs://hobby-matchmaker.firebasestorage.app") }

    singleOf(::AuthenticationRemoteDataSourceImpl) bind AuthenticationRemoteDataSource::class
    singleOf(::AuthenticationRepositoryImpl) bind AuthenticationRepository::class
}

expect val coreModuleAuthenticationPlatformSpecificData: Module
