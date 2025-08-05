package com.msoula.hobbymatchmaker.features.movies.presentation.di

import com.msoula.hobbymatchmaker.core.common.ErrorMessageProvider
import com.msoula.hobbymatchmaker.features.movies.presentation.mappers.MoviesErrorMessageProvider
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val featuresModuleMovieViewModel = module {
    single(named("moviesErrorMessageProvider")) {
        MoviesErrorMessageProvider()
    } bind ErrorMessageProvider::class

    includes(featuresModuleMovieViewModelPresentationPlatformSpecific)
}

expect val featuresModuleMovieViewModelPresentationPlatformSpecific: Module
