package com.msoula.hobbymatchmaker.core.authentication.domain.di

import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.LoginWithSocialMediaUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.ResetPasswordUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignInUseCase
import com.msoula.hobbymatchmaker.core.authentication.domain.use_cases.SignUpUseCase
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
        remoteDataSource: AuthenticationRemoteDataSource
    ): AuthenticationRepository = AuthenticationRepository(remoteDataSource)

    @Provides
    fun provideLogOutUseCase(
        authenticationRepository: AuthenticationRepository
    ): LogOutUseCase =
        LogOutUseCase(authenticationRepository)

    @Provides
    fun provideResetPasswordUseCase(authenticationRepository: AuthenticationRepository): ResetPasswordUseCase =
        ResetPasswordUseCase(authenticationRepository)

    @Provides
    fun provideSignInUseCase(
        authenticationRepository: AuthenticationRepository
    ): SignInUseCase =
        SignInUseCase(authenticationRepository)

    @Provides
    fun provideSignUpUseCase(authenticationRepository: AuthenticationRepository): SignUpUseCase =
        SignUpUseCase(authenticationRepository)

    @Provides
    fun provideLoginWithSocialMediaUsecase(authenticationRepository: AuthenticationRepository): LoginWithSocialMediaUseCase =
        LoginWithSocialMediaUseCase(authenticationRepository)
}
