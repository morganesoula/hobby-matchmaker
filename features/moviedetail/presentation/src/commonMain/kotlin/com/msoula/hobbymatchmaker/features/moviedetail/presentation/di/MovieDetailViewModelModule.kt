package com.msoula.hobbymatchmaker.features.moviedetail.presentation.di

import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featuresModuleMovieDetailViewModel = module {
    viewModel { (movieId: Long) ->
        MovieDetailViewModel(movieId = movieId, get(), get(), get())
    }
}
