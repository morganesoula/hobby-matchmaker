package com.msoula.hobbymatchmaker.features.movies.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module

val featuresModuleMovieViewModel = module {
    includes(featuresModuleMovieViewModelPresentationPlatformSpecific)
}

expect val featuresModuleMovieViewModelPresentationPlatformSpecific: Module
