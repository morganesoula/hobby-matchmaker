package com.msoula.hobbymatchmaker.features.moviedetail.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module

val featuresModuleMovieDetailViewModel = module {
    includes(featuresModuleMovieDetailPresentationPlatformSpecific)
}

expect val featuresModuleMovieDetailPresentationPlatformSpecific: Module
