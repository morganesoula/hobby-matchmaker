package com.msoula.hobbymatchmaker.features.moviedetail.presentation.di

import com.msoula.hobbymatchmaker.core.network.AndroidNetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val featuresModuleMovieDetailPresentationPlatformSpecific = module {
    viewModel { (movieId: Long) ->
        MovieDetailViewModel(
            movieId = movieId,
            get(),
            get(),
            get(),
            connectivityCheck = AndroidNetworkConnectivityChecker(get())
        )
    }
}
