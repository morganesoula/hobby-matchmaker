package com.msoula.hobbymatchmaker.core.common.di

import android.content.Context
import com.msoula.hobbymatchmaker.core.common.AuthenticationDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesAuthenticationDataStore(@ApplicationContext context: Context): AuthenticationDataStore {
        return AuthenticationDataStore(context)
    }
}
