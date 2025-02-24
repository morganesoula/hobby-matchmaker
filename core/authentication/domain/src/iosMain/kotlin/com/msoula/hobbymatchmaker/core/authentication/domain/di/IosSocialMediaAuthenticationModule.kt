package com.msoula.hobbymatchmaker.core.authentication.domain.di

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.IosAuthenticationRepository
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.IosAuthenticationRepositoryImpl
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.IosSocialMediaSignInUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val iosSocialMediaAuthenticationModule = module {
    singleOf(::IosAuthenticationRepositoryImpl) bind IosAuthenticationRepository::class
    factoryOf(::IosSocialMediaSignInUseCase)
}
