package com.msoula.hobbymatchmaker.features.movies.presentation.di

import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import org.koin.dsl.module

actual val featuresModuleMovieViewModelPresentationPlatformSpecific = module {
    single {
        MovieViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            //TODO Change for iOS version
            get(),
            get()
        )
    }
}
