package com.msoula.hobbymatchmaker.features.profile.domain.di

import com.msoula.hobbymatchmaker.features.profile.domain.useCases.GetCurrentUserProfileUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.UpdateUserProfileUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleUserProfileDomain = module {
    factoryOf(::GetCurrentUserProfileUseCase)
    factoryOf(::UpdateUserProfileUseCase)
}
