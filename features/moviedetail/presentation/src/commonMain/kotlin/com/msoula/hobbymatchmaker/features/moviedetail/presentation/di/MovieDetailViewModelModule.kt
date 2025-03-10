package com.msoula.hobbymatchmaker.features.moviedetail.presentation.di

import com.msoula.hobbymatchmaker.features.moviedetail.presentation.MovieDetailViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val featuresModuleMovieDetailViewModel = module {
    singleOf(::MovieDetailViewModel)
}
