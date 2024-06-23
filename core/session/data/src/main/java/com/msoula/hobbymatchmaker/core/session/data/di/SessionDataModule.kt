package com.msoula.hobbymatchmaker.core.session.data.di

import android.content.Context
import com.msoula.hobbymatchmaker.core.session.data.data_sources.local.SessionLocalDataSourceImpl
import com.msoula.hobbymatchmaker.core.session.domain.data_sources.SessionLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SessionDataModule {

    @Provides
    fun provideSessionLocalDataSource(@ApplicationContext context: Context): SessionLocalDataSource =
        SessionLocalDataSourceImpl(context)
}
