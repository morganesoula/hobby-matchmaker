package com.msoula.hobbymatchmaker.features.movies.data.di

import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.ImageRepositoryImpl
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val featuresModuleMovieDataPlatformSpecific = module {
    singleOf(::ImageRepositoryImpl) bind ImageRepository::class
}

