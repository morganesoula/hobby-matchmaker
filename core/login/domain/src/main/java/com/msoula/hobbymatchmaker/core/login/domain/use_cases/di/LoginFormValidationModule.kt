package com.msoula.hobbymatchmaker.core.login.domain.use_cases.di

import com.msoula.hobbymatchmaker.core.login.domain.use_cases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.domain.use_cases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.login.domain.use_cases.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.login.domain.use_cases.ValidatePasswordUseCase
import org.koin.dsl.module

val loginFormValidationModule = module {
    factory {
        LoginValidateFormUseCase(
            ValidateEmailUseCase(),
            ValidatePasswordUseCase(),
            ValidateNameUseCase()
        )
    }
}
