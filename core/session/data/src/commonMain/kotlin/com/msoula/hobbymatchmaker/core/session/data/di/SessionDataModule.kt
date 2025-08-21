package com.msoula.hobbymatchmaker.core.session.data.di

import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.SessionRemoteDataSource
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.SessionRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.core.session.data.repositories.SessionRepositoryImpl
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleSessionData = module {
    includes(coreModuleSessionDataPlatformSpecific)

    single<FirebaseFirestore> { Firebase.firestore }
    singleOf(::SessionRemoteDataSourceImpl) bind SessionRemoteDataSource::class
    singleOf(::SessionRepositoryImpl) bind SessionRepository::class
}

expect val coreModuleSessionDataPlatformSpecific: Module
