package com.msoula.hobbymatchmaker.features.moviedetail.presentation.di

import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.features.moviedetail.mappers.MovieDetailErrorMessageProvider
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val featuresModuleMovieDetailViewModel = module {
    single(named("movieDetailErrorMessageProvider")) {
        MovieDetailErrorMessageProvider()
    } bind ErrorMessageProvider::class

    includes(featuresModuleMovieDetailPresentationPlatformSpecific)
}

expect val featuresModuleMovieDetailPresentationPlatformSpecific: Module
