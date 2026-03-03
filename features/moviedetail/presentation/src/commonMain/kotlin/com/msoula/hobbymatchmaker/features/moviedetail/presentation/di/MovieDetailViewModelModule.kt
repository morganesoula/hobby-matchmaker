package com.msoula.hobbymatchmaker.features.moviedetail.presentation.di

import com.msoula.hobbymatchmaker.features.moviedetail.presentation.orchestrators.MovieDetailOrchestrator
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleMovieDetailViewModel = module {
    factoryOf(::MovieDetailOrchestrator)
    includes(featuresModuleMovieDetailPresentationPlatformSpecific)
}
expect val featuresModuleMovieDetailPresentationPlatformSpecific: Module
