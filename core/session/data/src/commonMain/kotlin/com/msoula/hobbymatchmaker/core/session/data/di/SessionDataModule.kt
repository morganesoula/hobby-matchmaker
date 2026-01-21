package com.msoula.hobbymatchmaker.core.session.data.di

import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.SessionLocalDataSource
import com.msoula.hobbymatchmaker.core.session.data.dataSources.local.SessionLocalDataSourceImpl
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.SessionRemoteDataSource
import com.msoula.hobbymatchmaker.core.session.data.dataSources.remote.SessionRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.core.session.data.repositories.SessionRepositoryImpl
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleSessionData = module {
    single { SessionLocalDataSourceImpl(get()) } bind SessionLocalDataSource::class
    singleOf(::SessionRemoteDataSourceImpl) bind SessionRemoteDataSource::class
    singleOf(::SessionRepositoryImpl) bind SessionRepository::class
}
