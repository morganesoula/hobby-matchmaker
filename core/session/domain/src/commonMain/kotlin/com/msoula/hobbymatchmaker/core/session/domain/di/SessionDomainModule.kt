package com.msoula.hobbymatchmaker.core.session.domain.di

import com.msoula.hobbymatchmaker.core.session.domain.useCases.ClearCurrentUserProfileUuidUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveDontAskCheckboxValueUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveSessionStateUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetCurrentUserProfileUuidUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetDontAskGuestDialogUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val coreModuleSessionDomain = module {
    factoryOf(::ObserveIsConnectedUseCase)
    factoryOf(::SetIsConnectedUseCase)
    factoryOf(::CreateUserUseCase)
    factoryOf(::SetDontAskGuestDialogUseCase)
    factoryOf(::ObserveDontAskCheckboxValueUseCase)
    factoryOf(::SetCurrentUserProfileUuidUseCase)
    factoryOf(::ObserveSessionStateUseCase)
    factoryOf(::ClearCurrentUserProfileUuidUseCase)
}
