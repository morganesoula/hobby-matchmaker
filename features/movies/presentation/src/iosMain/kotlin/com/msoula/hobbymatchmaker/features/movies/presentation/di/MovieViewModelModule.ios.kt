package com.msoula.hobbymatchmaker.features.movies.presentation.di

import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val featuresModuleMovieViewModelPresentationPlatformSpecific = module {
    viewModel {
        MovieViewModel(
            userActionOrchestrator = get(),
            defaultMessageMapper = get(),
            catalogOrchestrator = get()
        )
    }
}
