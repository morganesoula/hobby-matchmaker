package com.msoula.hobbymatchmaker.features.movies.presentation.di

import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.MoviesErrorMessageProvider
import org.koin.core.module.Module
import org.koin.dsl.module

val featuresModuleMovieViewModel = module {
    single { MoviesErrorMessageProvider() }

    includes(featuresModuleMovieViewModelPresentationPlatformSpecific)
}

expect val featuresModuleMovieViewModelPresentationPlatformSpecific: Module
