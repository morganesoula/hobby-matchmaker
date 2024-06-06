package com.msoula.hobbymatchmaker.core.session.domain.di

import com.msoula.hobbymatchmaker.core.common.AuthenticationDataStore
import com.msoula.hobbymatchmaker.core.session.domain.data_sources.UserLocalDataSource
import com.msoula.hobbymatchmaker.core.session.domain.repositories.UserRepository
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.ClearUserUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.FetchConnexionModeUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SaveAuthenticationStateUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SaveUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SessionModule {

    @Provides
    fun provideUserRepository(userLocalDataSource: UserLocalDataSource): UserRepository {
        return UserRepository(userLocalDataSource)
    }

    @Provides
    fun provideSaveAuthenticationStateUseCase(authenticationDataStore: AuthenticationDataStore): SaveAuthenticationStateUseCase {
        return SaveAuthenticationStateUseCase(authenticationDataStore)
    }

    @Provides
    fun provideFetchConnexionModeUseCase(authenticationDataStore: AuthenticationDataStore): FetchConnexionModeUseCase {
        return FetchConnexionModeUseCase(authenticationDataStore)
    }

    @Provides
    fun provideSaveUserUseCase(userRepository: UserRepository): SaveUserUseCase {
        return SaveUserUseCase(userRepository)
    }

    @Provides
    fun provideClearUserUseCase(userRepository: UserRepository): ClearUserUseCase {
        return ClearUserUseCase(userRepository)
    }
}
