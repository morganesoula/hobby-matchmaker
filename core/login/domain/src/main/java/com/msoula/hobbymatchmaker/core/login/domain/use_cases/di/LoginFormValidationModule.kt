package com.msoula.hobbymatchmaker.core.login.domain.use_cases.di

import com.msoula.hobbymatchmaker.core.login.domain.use_cases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.domain.use_cases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.login.domain.use_cases.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.login.domain.use_cases.ValidatePasswordUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LoginFormValidationModule {

    @Provides
    fun provideLoginFormValidationUseCase(): LoginValidateFormUseCase =
        LoginValidateFormUseCase(
            ValidateEmailUseCase(),
            ValidatePasswordUseCase(),
            ValidateNameUseCase()
        )
}
