package com.msoula.hobbymatchmaker.features.social.presentation

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featuresModuleSocialPresentation = module {
    viewModel {
        SocialViewModel(
            socialUseCases = get(),
            observeCurrentUser = get(),
            observeSessionStateUseCase = get(),
            defaultMessageMapper = get(),
            externalScope = null
        )
    }
}
