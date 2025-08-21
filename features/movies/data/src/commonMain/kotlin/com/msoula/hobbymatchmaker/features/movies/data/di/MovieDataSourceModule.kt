package com.msoula.hobbymatchmaker.features.movies.data.di

import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.MovieLocalDataSourceImpl
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.MovieRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBKtorService
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBKtorServiceImpl
import com.msoula.hobbymatchmaker.features.movies.data.repositories.MovieRepositoryImpl
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featuresModuleMovieData = module {
    includes(featuresModuleMovieDataPlatformSpecific)
    singleOf(::MovieRemoteDataSourceImpl) bind MovieRemoteDataSource::class
    singleOf(::MovieLocalDataSourceImpl) bind MovieLocalDataSource::class
    singleOf(::MovieRepositoryImpl) bind MovieRepository::class

    single<TMDBKtorService> { TMDBKtorServiceImpl(get()) }
}

expect val featuresModuleMovieDataPlatformSpecific: Module
