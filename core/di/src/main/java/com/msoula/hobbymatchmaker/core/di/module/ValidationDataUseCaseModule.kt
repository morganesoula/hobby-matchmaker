package com.msoula.hobbymatchmaker.core.di.module

import com.msoula.hobbymatchmaker.core.di.domain.useCase.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCase.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCase.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCase.ValidatePasswordUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val authValidationDataUseCaseModule = module {
    factory<AuthFormValidationUseCase> { AuthFormValidationUseCase(
        ValidateEmailUseCase(),
        ValidatePasswordUseCase(),
        ValidateNameUseCase(),
        ValidateNameUseCase()
    ) }
}
