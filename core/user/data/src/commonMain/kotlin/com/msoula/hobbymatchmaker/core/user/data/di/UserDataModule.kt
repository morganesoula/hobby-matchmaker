package com.msoula.hobbymatchmaker.core.user.data.di

import com.msoula.hobbymatchmaker.core.user.data.dataSources.local.UserLocalDataSource
import com.msoula.hobbymatchmaker.core.user.data.dataSources.local.UserLocalDataSourceImpl
import com.msoula.hobbymatchmaker.core.user.data.dataSources.remote.UserRemoteDataSource
import com.msoula.hobbymatchmaker.core.user.data.dataSources.remote.UserRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.core.user.data.repositories.UserDataRepositoryImpl
import com.msoula.hobbymatchmaker.core.user.domain.repositories.UserDataRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleUserData = module {
    singleOf(::UserLocalDataSourceImpl) bind UserLocalDataSource::class
    singleOf(::UserRemoteDataSourceImpl) bind UserRemoteDataSource::class
    singleOf(::UserDataRepositoryImpl) bind UserDataRepository::class
}
