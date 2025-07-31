package com.msoula.hobbymatchmaker.features.moviedetail.presentation.di

import com.msoula.hobbymatchmaker.features.moviedetail.mappers.MovieDetailErrorMessageProvider
import org.koin.core.module.Module
import org.koin.dsl.module

val featuresModuleMovieDetailViewModel = module {
    single { MovieDetailErrorMessageProvider() }
    includes(featuresModuleMovieDetailPresentationPlatformSpecific)
}

expect val featuresModuleMovieDetailPresentationPlatformSpecific: Module
