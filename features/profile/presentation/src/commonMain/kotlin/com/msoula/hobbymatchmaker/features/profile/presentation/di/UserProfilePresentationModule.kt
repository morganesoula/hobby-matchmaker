package com.msoula.hobbymatchmaker.features.profile.presentation.di

import com.msoula.hobbymatchmaker.features.profile.presentation.PickPseudoViewModel
import com.msoula.hobbymatchmaker.features.profile.presentation.UserProfileViewModel
import com.msoula.hobbymatchmaker.features.profile.presentation.orchestrators.UserProfileOrchestrator
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featuresModuleUserProfilePresentation = module {
    factoryOf(::UserProfileOrchestrator)

    viewModel {
        UserProfileViewModel(
            userProfileInteractor = get(),
            defaultMessageMapper = get(),
            logOutUseCase = get(),
            upsertUserUseCase = get(),
            syncUserUseCase = get(),
            refreshSocialCircleUseCase = get(),
            observeSessionStateUseCase = get(),
            externalScope = null
        )
    }

    viewModel {
        PickPseudoViewModel(
            observeSessionStateUseCase = get(),
            observeCurrentUserProfileStateUseCase = get(),
            isPseudoAvailableUseCase = get(),
            reservePseudoUseCase = get(),
            logOutUseCase = get(),
            defaultMessageMapper = get(),
            externalScope = null
        )
    }
}
