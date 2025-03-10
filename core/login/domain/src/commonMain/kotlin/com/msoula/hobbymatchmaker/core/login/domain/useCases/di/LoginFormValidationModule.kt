package com.msoula.hobbymatchmaker.core.login.domain.useCases.di

import com.msoula.hobbymatchmaker.core.login.domain.useCases.LoginValidateFormUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.login.domain.useCases.ValidatePasswordUseCase
import org.koin.dsl.module

val coreModuleLoginFormValidation = module {
    factory {
        LoginValidateFormUseCase(
            ValidateEmailUseCase(),
            ValidatePasswordUseCase(),
            ValidateNameUseCase()
        )
    }
}
