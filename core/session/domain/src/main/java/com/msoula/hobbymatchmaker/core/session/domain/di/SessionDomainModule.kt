package com.msoula.hobbymatchmaker.core.session.domain.di

import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.CreateUserUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SetIsConnectedUseCase
import org.koin.dsl.module

val sessionDomainModule = module {
    factory<SessionRepository> { SessionRepository(get(), get()) }
    factory<ObserveIsConnectedUseCase> { ObserveIsConnectedUseCase(get()) }
    factory<SetIsConnectedUseCase> { SetIsConnectedUseCase(get()) }
    factory<CreateUserUseCase> { CreateUserUseCase(get()) }
}
