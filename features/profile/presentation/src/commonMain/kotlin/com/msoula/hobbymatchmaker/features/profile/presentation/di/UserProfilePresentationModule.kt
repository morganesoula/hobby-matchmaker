package com.msoula.hobbymatchmaker.features.profile.presentation.di

import com.msoula.hobbymatchmaker.features.profile.presentation.UserProfileViewModel
import com.msoula.hobbymatchmaker.features.profile.presentation.interactors.SessionInteractor
import com.msoula.hobbymatchmaker.features.profile.presentation.interactors.UserProfileInteractor
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featuresModuleUserProfilePresentation = module {
    factoryOf(::UserProfileInteractor)
    factoryOf(::SessionInteractor)

    viewModel {
        UserProfileViewModel(
            userProfileInteractor = get(),
            sessionInteractor = get(),
            defaultMessageMapper = get(),
            externalScope = null
        )
    }
}
