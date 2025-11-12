package com.msoula.hobbymatchmaker.features.movies.presentation.di

import com.msoula.hobbymatchmaker.core.network.AndroidNetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val featuresModuleMovieViewModelPresentationPlatformSpecific = module {
    viewModel {
        MovieViewModel(
            setMovieFavoriteUseCase = get(),
            observeAllMoviesUseCase = get(),
            fetchFirebaseUserInfo = get(),
            logOutUseCase = get(),
            checkMovieSynopsisValueUseCase = get(),
            connectivityCheck = AndroidNetworkConnectivityChecker(get()),
            defaultMessageMapper = get()
        )
    }
}
