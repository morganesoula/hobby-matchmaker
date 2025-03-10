package com.msoula.hobbymatchmaker.features.movies.presentation.di

import com.msoula.hobbymatchmaker.features.movies.presentation.MovieViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val featuresModuleMovieViewModel = module {
    singleOf(::MovieViewModel)
}
