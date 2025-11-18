package com.msoula.hobbymatchmaker.features.profile.presentation.di

import com.msoula.hobbymatchmaker.features.profile.presentation.UserProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featuresModuleUserProfilePresentation = module {
    viewModel {
        UserProfileViewModel(
            observeCurrentUserProfileStateUseCase = get(),
            observeSessionStateUseCase = get(),
            upsertUserProfileUseCase = get(),
            logOutUseCase = get(),
            defaultMessageMapper = get(),
            externalScope = null
        )
    }
}
