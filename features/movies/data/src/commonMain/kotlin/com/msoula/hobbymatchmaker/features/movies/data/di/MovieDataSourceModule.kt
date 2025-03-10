package com.msoula.hobbymatchmaker.features.movies.data.di

import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.MovieLocalDataSourceImpl
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBKtorService
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBKtorServiceImpl
import com.msoula.hobbymatchmaker.features.movies.domain.dataSources.MovieLocalDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featuresModuleMovieData = module {
    includes(featuresModuleMovieDataPlatformSpecific)

    singleOf(::MovieLocalDataSourceImpl) bind MovieLocalDataSource::class
    singleOf(::MovieLocalDataSourceImpl) bind MovieLocalDataSource::class
    single<TMDBKtorService> { TMDBKtorServiceImpl(get()) }
}

expect val featuresModuleMovieDataPlatformSpecific: Module
