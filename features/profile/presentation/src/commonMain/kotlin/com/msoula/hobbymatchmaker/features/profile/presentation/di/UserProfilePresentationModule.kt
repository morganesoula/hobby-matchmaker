package com.msoula.hobbymatchmaker.features.profile.presentation.di

import com.msoula.hobbymatchmaker.features.profile.presentation.UserProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featuresModuleUserProfilePresentation = module {
    viewModel {
        UserProfileViewModel(
            get(),
            get(),
            get(),
            get(),
            null
        )
    }
}
