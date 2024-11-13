package com.msoula.hobbymatchmaker.core.session.domain.di

import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val sessionDomainModule = module {
    factoryOf(::SessionRepository)
    factoryOf(::ObserveIsConnectedUseCase)
    factoryOf(::SetIsConnectedUseCase)
    factoryOf(::CreateUserUseCase)
}
