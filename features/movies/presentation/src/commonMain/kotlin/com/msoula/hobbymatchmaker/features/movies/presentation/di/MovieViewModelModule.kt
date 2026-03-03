package com.msoula.hobbymatchmaker.features.movies.presentation.di

import com.msoula.hobbymatchmaker.features.movies.presentation.orchestrators.MovieCatalogOrchestrator
import com.msoula.hobbymatchmaker.features.movies.presentation.orchestrators.MovieUserActionOrchestrator
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleMovieViewModel = module {
    factoryOf(::MovieUserActionOrchestrator)
    factoryOf(::MovieCatalogOrchestrator)
    includes(featuresModuleMovieViewModelPresentationPlatformSpecific)
}
expect val featuresModuleMovieViewModelPresentationPlatformSpecific: Module
