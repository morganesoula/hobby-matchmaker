package com.msoula.hobbymatchmaker.features.moviedetail.presentation.di

import com.msoula.hobbymatchmaker.core.network.AndroidNetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val featuresModuleMovieDetailPresentationPlatformSpecific = module {
    viewModel { (movieId: Long) ->
        MovieDetailViewModel(
            movieId = movieId,
            ioDispatcher = get(),
            observeMovieDetailUseCase = get(),
            manageMovieTrailerUseCase = get(),
            connectivityCheck = AndroidNetworkConnectivityChecker(get()),
            defaultErrorMessageMapper = get()
        )
    }
}
