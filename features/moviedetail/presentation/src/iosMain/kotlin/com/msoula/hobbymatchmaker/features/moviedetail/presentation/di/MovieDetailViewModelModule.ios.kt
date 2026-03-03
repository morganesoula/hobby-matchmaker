package com.msoula.hobbymatchmaker.features.moviedetail.presentation.di

import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual val featuresModuleMovieDetailPresentationPlatformSpecific = module {
    viewModel { (movieId: Long) ->
        MovieDetailViewModel(
            movieId = movieId,
            interactor = get(),
            syncFavoriteToCircleUseCase = get(),
            checkMovieMatchUseCase = get(),
            defaultMessageMapper = get()
        )
    }
}
