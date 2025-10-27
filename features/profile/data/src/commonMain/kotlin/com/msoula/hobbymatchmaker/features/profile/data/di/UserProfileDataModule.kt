package com.msoula.hobbymatchmaker.features.profile.data.di

import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.SocialLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.SocialLocalDataSourceImpl
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.UserProfileLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.UserProfileLocalDataSourceImpl
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.SocialRemoteDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.SocialRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.UserProfileRemoteDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.UserProfileRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.features.profile.data.repositories.SocialRepositoryImpl
import com.msoula.hobbymatchmaker.features.profile.data.repositories.UserProfileRepositoryImpl
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.SocialRepository
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featuresModuleUserProfileData = module {
    singleOf(::UserProfileLocalDataSourceImpl) bind UserProfileLocalDataSource::class
    singleOf(::UserProfileRemoteDataSourceImpl) bind UserProfileRemoteDataSource::class
    singleOf(::SocialLocalDataSourceImpl) bind SocialLocalDataSource::class
    singleOf(::SocialRemoteDataSourceImpl) bind SocialRemoteDataSource::class
    singleOf(::UserProfileRepositoryImpl) bind UserProfileRepository::class
    singleOf(::SocialRepositoryImpl) bind SocialRepository::class
}
