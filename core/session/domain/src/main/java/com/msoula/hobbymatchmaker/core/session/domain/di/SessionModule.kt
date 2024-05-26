package com.msoula.hobbymatchmaker.core.session.domain.di

import com.msoula.hobbymatchmaker.core.common.AuthenticationDataStore
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SaveAuthenticationStateUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SessionModule {

    @Provides
    fun provideSaveAuthenticationStateUseCase(authenticationDataStore: AuthenticationDataStore): SaveAuthenticationStateUseCase {
        return SaveAuthenticationStateUseCase(authenticationDataStore)
    }
}
