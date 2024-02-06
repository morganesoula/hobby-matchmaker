package com.msoula.di.module

import android.content.Context
import com.msoula.di.data.StringResourcesProviderImpl
import com.msoula.di.domain.StringResourcesProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ResourceModule {

    @Provides
    @Singleton
    fun provideResourceProvider(@ApplicationContext context: Context): StringResourcesProvider =
        StringResourcesProviderImpl(context)
}