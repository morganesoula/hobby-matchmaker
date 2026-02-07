package com.msoula.hobbymatchmaker.features.profile.data.di

import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.UserProfileLocalDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.local.UserProfileLocalDataSourceImpl
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.UserProfileRemoteDataSource
import com.msoula.hobbymatchmaker.features.profile.data.dataSources.remote.UserProfileRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.features.profile.data.repositories.UserProfileRepositoryImpl
import com.msoula.hobbymatchmaker.features.profile.domain.repositories.UserProfileRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featuresModuleUserProfileData = module {
    single<UserProfileLocalDataSource>(createdAtStart = true) { UserProfileLocalDataSourceImpl(get()) }
    singleOf(::UserProfileRemoteDataSourceImpl) bind UserProfileRemoteDataSource::class
    single<UserProfileRepository>(createdAtStart = true) {
        UserProfileRepositoryImpl(
            get(),
            get()
        )
    }
}
