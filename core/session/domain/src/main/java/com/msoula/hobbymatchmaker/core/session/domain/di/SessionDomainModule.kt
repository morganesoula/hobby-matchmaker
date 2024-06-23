package com.msoula.hobbymatchmaker.core.session.domain.di

import com.msoula.hobbymatchmaker.core.session.domain.data_sources.SessionLocalDataSource
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.ClearSessionDataUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.GetConnexionModeUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SaveUserUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SetIsConnectedUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SessionDomainModule {

    @Provides
    fun provideSessionRepository(sessionLocalDataSource: SessionLocalDataSource): SessionRepository {
        return SessionRepository(sessionLocalDataSource)
    }

    @Provides
    fun provideObserveIsConnectedUseCase(sessionRepository: SessionRepository): ObserveIsConnectedUseCase {
        return ObserveIsConnectedUseCase(sessionRepository)
    }

    @Provides
    fun provideSetIsConnectedUseCase(sessionRepository: SessionRepository): SetIsConnectedUseCase {
        return SetIsConnectedUseCase(sessionRepository)
    }

    @Provides
    fun provideGetConnexionModeUseCase(sessionRepository: SessionRepository): GetConnexionModeUseCase {
        return GetConnexionModeUseCase(sessionRepository)
    }

    @Provides
    fun provideSaveUserUseCase(sessionRepository: SessionRepository): SaveUserUseCase {
        return SaveUserUseCase(sessionRepository)
    }

    @Provides
    fun provideClearSessionDataUseCase(sessionRepository: SessionRepository): ClearSessionDataUseCase {
        return ClearSessionDataUseCase(sessionRepository)
    }
}
