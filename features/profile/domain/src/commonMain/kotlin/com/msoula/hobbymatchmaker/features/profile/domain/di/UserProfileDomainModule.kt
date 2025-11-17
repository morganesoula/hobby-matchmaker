package com.msoula.hobbymatchmaker.features.profile.domain.di

import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileStateUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.RefreshUserProfileUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.UpdateUserProfileUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleUserProfileDomain = module {
    factoryOf(::RefreshUserProfileUseCase)
    factoryOf(::ObserveCurrentUserProfileStateUseCase)
    factoryOf(::UpdateUserProfileUseCase)
}
