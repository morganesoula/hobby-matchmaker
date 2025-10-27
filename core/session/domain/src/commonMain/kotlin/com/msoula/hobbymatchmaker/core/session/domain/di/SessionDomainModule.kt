package com.msoula.hobbymatchmaker.core.session.domain.di

import com.msoula.hobbymatchmaker.core.session.domain.useCases.CreateUserUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveShouldShowGuestDialogUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetCurrentUserProfileUuidUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.SetShouldShowGuestDialogUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val coreModuleSessionDomain = module {
    factoryOf(::ObserveIsConnectedUseCase)
    factoryOf(::SetIsConnectedUseCase)
    factoryOf(::CreateUserUseCase)
    factoryOf(::SetShouldShowGuestDialogUseCase)
    factoryOf(::ObserveShouldShowGuestDialogUseCase)
    factoryOf(::SetCurrentUserProfileUuidUseCase)
}
