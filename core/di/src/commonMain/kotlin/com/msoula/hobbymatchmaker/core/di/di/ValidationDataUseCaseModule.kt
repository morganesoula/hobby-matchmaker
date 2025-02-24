package com.msoula.hobbymatchmaker.core.di.di

import com.msoula.hobbymatchmaker.core.di.domain.useCases.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidatePasswordUseCase
import org.koin.dsl.module

val authValidationDataUseCaseModule = module {
    factory { ValidateEmailUseCase() }
    factory { ValidateNameUseCase() }
    factory { ValidatePasswordUseCase() }

    factory { AuthFormValidationUseCase(get(), get(), get(), get()) }
}
