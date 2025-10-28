package com.msoula.hobbymatchmaker.features.profile.presentation.di

import com.msoula.hobbymatchmaker.features.profile.presentation.UserProfileViewModel
import org.koin.dsl.module

val featuresModuleUserProfilePresentation = module {
    single {
        UserProfileViewModel(
            get(),
            get(),
            get(),
            null
        )
    }
}
