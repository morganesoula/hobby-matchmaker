package com.msoula.hobbymatchmaker.features.settings.presentation.di

import com.msoula.hobbymatchmaker.features.settings.presentation.SettingsViewModel
import com.msoula.hobbymatchmaker.features.settings.presentation.orchestrators.SettingsOrchestrator
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featuresModuleSettingsPresentation = module {
    factoryOf(::SettingsOrchestrator)

    viewModel {
        SettingsViewModel(
            settingsInteractor = get(),
            defaultMessageMapper = get(),
            externalScope = null
        )
    }
}
