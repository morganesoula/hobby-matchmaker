package com.msoula.hobbymatchmaker.features.movies.presentation.di

import com.msoula.hobbymatchmaker.features.movies.presentation.interactors.MovieInteractor
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleMovieViewModel = module {
    factoryOf(::MovieInteractor)
    includes(featuresModuleMovieViewModelPresentationPlatformSpecific)
}
expect val featuresModuleMovieViewModelPresentationPlatformSpecific: Module
