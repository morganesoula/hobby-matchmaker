package com.msoula.hobbymatchmaker.features.movies.presentation.di

import com.msoula.hobbymatchmaker.core.network.AndroidNetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val featuresModuleMovieViewModelPresentationPlatformSpecific = module {
    single {
        MovieViewModel(
            setMovieFavoriteUseCase = get(),
            observeAllMoviesUseCase = get(),
            getUserInfo = get(),
            logOutUseCase = get(),
            checkMovieSynopsisValueUseCase = get(),
            connectivityCheck = AndroidNetworkConnectivityChecker(get()),
            ioDispatcher = get(),
            errorMessageProvider = get(named("moviesErrorMessageProvider"))
        )
    }
}
