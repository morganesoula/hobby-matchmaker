package com.msoula.hobbymatchmaker.features.movies.presentation.di

import com.msoula.hobbymatchmaker.core.network.IOSNetworkConnectivityChecker
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
            connectivityCheck = IOSNetworkConnectivityChecker(),
            get(),
        )
    }
}
