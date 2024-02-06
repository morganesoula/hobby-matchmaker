package com.msoula.di.module

import com.msoula.di.domain.use_case.AuthFormValidationUseCase
import com.msoula.di.domain.use_case.ValidateEmail
import com.msoula.di.domain.use_case.ValidateName
import com.msoula.di.domain.use_case.ValidatePassword
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
            ValidateName()
        )
}