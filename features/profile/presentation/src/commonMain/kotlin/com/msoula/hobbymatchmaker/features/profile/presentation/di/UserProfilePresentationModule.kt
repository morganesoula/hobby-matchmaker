package com.msoula.hobbymatchmaker.features.profile.presentation.di

import com.msoula.hobbymatchmaker.features.profile.presentation.UserProfileViewModel
import com.msoula.hobbymatchmaker.features.profile.presentation.interactors.UserProfileInteractor
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featuresModuleUserProfilePresentation = module {
    factoryOf(::UserProfileInteractor)

    viewModel {
        UserProfileViewModel(
            interactor = get(),
            observeCurrentUserProfileStateUseCase = get(),
            observeSessionStateUseCase = get(),
            defaultMessageMapper = get(),
            externalScope = null
        )
    }
}
