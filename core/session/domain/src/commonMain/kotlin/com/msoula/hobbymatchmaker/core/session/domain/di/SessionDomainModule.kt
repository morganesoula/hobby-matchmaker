package com.msoula.hobbymatchmaker.core.session.domain.di

import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepositoryImpl
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModuleSessionDomain = module {
    factoryOf(::SessionRepositoryImpl) bind SessionRepository::class
    factoryOf(::ObserveIsConnectedUseCase)
    factoryOf(::SetIsConnectedUseCase)
    factoryOf(::CreateUserUseCase)
}
