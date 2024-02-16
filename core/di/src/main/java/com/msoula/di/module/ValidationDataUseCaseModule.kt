package com.msoula.di.module

import com.msoula.di.domain.useCase.AuthFormValidationUseCase
import com.msoula.di.domain.useCase.ValidateEmail
import com.msoula.di.domain.useCase.ValidateName
import com.msoula.di.domain.useCase.ValidatePassword
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ValidationDataUseCaseModule {
    @Provides
    fun provideAuthFormValidationUseCase(): AuthFormValidationUseCase =
        AuthFormValidationUseCase(
            ValidateEmail(),
            ValidatePassword(),
            ValidateName(),
            ValidateName(),
        )
}
