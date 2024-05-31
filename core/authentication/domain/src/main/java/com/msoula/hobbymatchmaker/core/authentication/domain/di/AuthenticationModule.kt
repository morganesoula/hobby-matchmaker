package com.msoula.hobbymatchmaker.core.authentication.domain.di

import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationLocalDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.LoginWithFacebookUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.LoginWithGoogleUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.ObserveAuthenticationStateUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignUpUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SaveAuthenticationStateUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

    @Provides
    @Singleton
    fun provideAuthenticationRepository(
        remoteDataSource: AuthenticationRemoteDataSource,
        localDataSource: AuthenticationLocalDataSource
    ): AuthenticationRepository = AuthenticationRepository(remoteDataSource, localDataSource)

    @Provides
    fun provideLogOutUseCase(
        authenticationRepository: AuthenticationRepository,
        saveAuthenticationStateUseCase: SaveAuthenticationStateUseCase
    ): LogOutUseCase =
        LogOutUseCase(authenticationRepository, saveAuthenticationStateUseCase)

    @Provides
    fun provideResetPasswordUseCase(authenticationRepository: AuthenticationRepository): ResetPasswordUseCase =
        ResetPasswordUseCase(authenticationRepository)

    @Provides
    fun provideSignInUseCase(
        authenticationRepository: AuthenticationRepository,
        saveAuthenticationStateUseCase: SaveAuthenticationStateUseCase
    ): SignInUseCase =
        SignInUseCase(authenticationRepository, saveAuthenticationStateUseCase)

    @Provides
    fun provideSignUpUseCase(authenticationRepository: AuthenticationRepository): SignUpUseCase =
        SignUpUseCase(authenticationRepository)

    @Provides
    fun provideObserveAuthenticationStateUseCase(authenticationRepository: AuthenticationRepository): ObserveAuthenticationStateUseCase =
        ObserveAuthenticationStateUseCase(authenticationRepository)

    @Provides
    fun provideLoginWithFacebookUseCase(authenticationRepository: AuthenticationRepository): LoginWithFacebookUseCase =
        LoginWithFacebookUseCase(authenticationRepository)

    @Provides
    fun provideLoginWithGoogleUseCase(authenticationRepository: AuthenticationRepository): LoginWithGoogleUseCase =
        LoginWithGoogleUseCase(authenticationRepository)
}
