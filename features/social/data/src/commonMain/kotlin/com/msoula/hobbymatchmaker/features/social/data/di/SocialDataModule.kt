package com.msoula.hobbymatchmaker.features.social.data.di

import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.SocialLocalDataSource
import com.msoula.hobbymatchmaker.features.social.data.dataSources.local.SocialLocalDataSourceImpl
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.SocialRemoteDataSource
import com.msoula.hobbymatchmaker.features.social.data.dataSources.remote.SocialRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.features.social.data.repositories.SocialRepositoryImpl
import com.msoula.hobbymatchmaker.features.social.domain.repositories.SocialRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featuresModuleSocialData = module {
    singleOf(::SocialLocalDataSourceImpl) bind SocialLocalDataSource::class
    singleOf(::SocialRemoteDataSourceImpl) bind SocialRemoteDataSource::class
    singleOf(::SocialRepositoryImpl) bind SocialRepository::class
}
