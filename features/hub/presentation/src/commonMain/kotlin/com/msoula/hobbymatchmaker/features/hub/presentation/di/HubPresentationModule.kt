package com.msoula.hobbymatchmaker.features.hub.presentation.di

import com.msoula.hobbymatchmaker.features.hub.presentation.HubViewModel
import com.msoula.hobbymatchmaker.features.hub.presentation.orchestrators.HubOrchestrator
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featuresModuleHubPresentation = module {
    factoryOf(::HubOrchestrator)

    viewModel {
        HubViewModel(
            hubInteractor = get(),
            defaultMessageMapper = get(),
            externalScope = null
        )
    }
}
