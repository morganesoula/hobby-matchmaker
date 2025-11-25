package com.msoula.hobbymatchmaker.features.moviedetail.presentation.di

import com.msoula.hobbymatchmaker.features.moviedetail.presentation.interactors.MovieDetailInteractor
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleMovieDetailViewModel = module {
    factoryOf(::MovieDetailInteractor)
    includes(featuresModuleMovieDetailPresentationPlatformSpecific)
}
expect val featuresModuleMovieDetailPresentationPlatformSpecific: Module
