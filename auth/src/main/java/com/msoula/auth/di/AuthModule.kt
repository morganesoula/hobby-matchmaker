package com.msoula.auth.di

import com.google.firebase.auth.FirebaseAuth
import com.msoula.auth.data.AuthRepositoryImpl
import com.msoula.auth.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    @Named("authInstance")
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    @Named("authRepository")
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl(provideFirebaseAuth())
}