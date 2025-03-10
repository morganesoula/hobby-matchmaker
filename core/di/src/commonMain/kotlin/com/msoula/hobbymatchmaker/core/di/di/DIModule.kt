package com.msoula.hobbymatchmaker.core.di.di

import com.msoula.hobbymatchmaker.core.di.domain.useCases.AuthFormValidationUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidateEmailUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidateNameUseCase
import com.msoula.hobbymatchmaker.core.di.domain.useCases.ValidatePasswordUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModuleDI = module {
    includes(coreModuleDIPlatformSpecific)

    single<CoroutineDispatcher> { Dispatchers.IO }
    factory { ValidateEmailUseCase() }
    factory { ValidateNameUseCase() }
    factory { ValidatePasswordUseCase() }

    factory { AuthFormValidationUseCase(get(), get(), get(), get()) }
}

expect val coreModuleDIPlatformSpecific: Module
