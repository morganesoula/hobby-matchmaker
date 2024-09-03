package com.msoula.hobbymatchmaker.core.session.domain.di

import com.msoula.hobbymatchmaker.core.session.domain.repositories.SessionRepository
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.ClearSessionDataUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.GetConnexionModeUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SaveUserUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SetIsConnectedUseCase
import org.koin.dsl.module

val sessionDomainModule = module {
    factory<SessionRepository> { SessionRepository(get()) }
    factory<ObserveIsConnectedUseCase> { ObserveIsConnectedUseCase(get()) }
    factory<SetIsConnectedUseCase> { SetIsConnectedUseCase(get()) }
    factory<GetConnexionModeUseCase> { GetConnexionModeUseCase(get()) }
    factory<ClearSessionDataUseCase> { ClearSessionDataUseCase(get()) }
    factory<SaveUserUseCase> { SaveUserUseCase(get()) }
}
