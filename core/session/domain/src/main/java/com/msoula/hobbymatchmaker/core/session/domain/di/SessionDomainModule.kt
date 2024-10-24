package com.msoula.hobbymatchmaker.core.session.domain.di

import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import org.koin.dsl.module

val sessionDomainModule = module {
    factory<SessionRepository> { SessionRepository(get(), get()) }
    factory<ObserveIsConnectedUseCase> { ObserveIsConnectedUseCase(get()) }
    factory<SetIsConnectedUseCase> { SetIsConnectedUseCase(get()) }
    factory<CreateUserUseCase> { CreateUserUseCase(get()) }
}
