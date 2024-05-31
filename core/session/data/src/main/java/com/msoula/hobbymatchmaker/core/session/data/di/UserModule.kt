package com.msoula.hobbymatchmaker.core.session.data.di

import com.msoula.hobbymatchmaker.core.common.AuthenticationDataStore
import com.msoula.hobbymatchmaker.core.session.data.UserLocalDataSourceImpl
import com.msoula.hobbymatchmaker.core.session.domain.data_sources.UserLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Provides
    fun provideUserLocalDataSource(authenticationDataStore: AuthenticationDataStore): UserLocalDataSource =
        UserLocalDataSourceImpl(authenticationDataStore)
}
