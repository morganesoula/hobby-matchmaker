package com.msoula.hobbymatchmaker.features.profile.domain.di

import com.msoula.hobbymatchmaker.features.profile.domain.useCases.CreateDefaultUserProfileUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.IsPseudoAvailableUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.SyncUserProfileUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.UpsertUserProfileUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleUserProfileDomain = module {
    factoryOf(::SyncUserProfileUseCase)
    factoryOf(::ObserveCurrentUserProfileStateUseCase)
    factoryOf(::UpsertUserProfileUseCase)
    factoryOf(::CreateDefaultUserProfileUseCase)
    factoryOf(::IsPseudoAvailableUseCase)
}
