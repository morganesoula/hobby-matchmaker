package com.msoula.hobbymatchmaker.features.movies.data.di

import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.ImageRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

//TODO Bind ImageRepository
actual val featuresModuleMovieDataPlatformSpecific = module {
    singleOf(::ImageRepositoryImpl)
}

