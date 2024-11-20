package com.msoula.hobbymatchmaker.core.di.module

import com.msoula.hobbymatchmaker.core.di.domain.useCase.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCase.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCase.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCase.ValidatePasswordUseCase
import org.koin.dsl.module

val authValidationDataUseCaseModule = module {
    factory { ValidateEmailUseCase() }
    factory { ValidateNameUseCase() }
    factory { ValidatePasswordUseCase() }

    factory { AuthFormValidationUseCase(get(), get(), get(), get()) }
}
