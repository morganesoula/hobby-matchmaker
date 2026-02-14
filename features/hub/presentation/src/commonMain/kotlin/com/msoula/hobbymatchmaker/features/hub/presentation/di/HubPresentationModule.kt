package com.msoula.hobbymatchmaker.features.hub.presentation.di

import com.msoula.hobbymatchmaker.features.hub.presentation.HubViewModel
import com.msoula.hobbymatchmaker.features.hub.presentation.interactors.HubInteractor
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featuresModuleHubPresentation = module {
    factoryOf(::HubInteractor)

    viewModel {
        HubViewModel(
            hubInteractor = get(),
            defaultMessageMapper = get(),
            externalScope = null
        )
    }
}
