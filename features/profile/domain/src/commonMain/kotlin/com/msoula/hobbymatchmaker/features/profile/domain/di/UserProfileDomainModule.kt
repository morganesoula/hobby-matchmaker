package com.msoula.hobbymatchmaker.features.profile.domain.di

import com.msoula.hobbymatchmaker.features.profile.domain.useCases.ObserveCurrentUserProfileUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.RefreshUserProfileUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleUserProfileDomain = module {
    factoryOf(::ObserveCurrentUserProfileUseCase)
    factoryOf(::RefreshUserProfileUseCase)
}
