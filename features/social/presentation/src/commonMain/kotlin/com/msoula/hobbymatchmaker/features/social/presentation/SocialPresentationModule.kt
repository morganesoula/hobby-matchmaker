package com.msoula.hobbymatchmaker.features.social.presentation

import org.koin.core.module.dsl.viewModel
import com.msoula.hobbymatchmaker.features.social.presentation.interactors.SocialInteractor
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleSocialPresentation = module {
    factoryOf(::SocialInteractor)

    viewModel {
        SocialViewModel(
            interactor = get(),
            observeSessionStateUseCase = get(),
            defaultMessageMapper = get(),
            externalScope = null
        )
    }
}
