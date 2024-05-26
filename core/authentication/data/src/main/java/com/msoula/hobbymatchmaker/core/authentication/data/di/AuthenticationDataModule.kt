package com.msoula.hobbymatchmaker.core.authentication.data.di

import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.local.AuthenticationLocalDataSourceImpl
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.AuthenticationRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationLocalDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.common.AuthenticationDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationDataModule {

    @Provides
    @Singleton
    fun provideAuthenticationRemoteDataSource(
        auth: FirebaseAuth,
        signInClient: SignInClient
    ): AuthenticationRemoteDataSource =
        AuthenticationRemoteDataSourceImpl(auth, signInClient)

    @Provides
    @Singleton
    fun provideAuthenticationLocalDataSource(authenticationDataStore: AuthenticationDataStore): AuthenticationLocalDataSource =
        AuthenticationLocalDataSourceImpl(authenticationDataStore)
}
