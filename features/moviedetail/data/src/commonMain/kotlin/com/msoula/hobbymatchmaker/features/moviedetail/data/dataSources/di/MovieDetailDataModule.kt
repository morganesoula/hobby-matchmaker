package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.di

import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.MovieDetailLocalDataSourceImpl
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.MovieDetailRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieDetailKtorService
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieDetailKtorServiceImpl
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieVideosKtorService
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieVideosKtorServiceImpl
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.repositories.MovieDetailRepositoryImpl
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featuresModuleMovieDetailData = module {
    singleOf(::MovieDetailKtorServiceImpl) bind MovieDetailKtorService::class
    singleOf(::MovieVideosKtorServiceImpl) bind MovieVideosKtorService::class
    singleOf(::MovieDetailLocalDataSourceImpl) bind MovieDetailLocalDataSource::class
    singleOf(::MovieDetailRemoteDataSourceImpl) bind MovieDetailRemoteDataSource::class

    singleOf(::MovieDetailRepositoryImpl) bind MovieDetailRepository::class
}
