package com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.di

import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.local.MovieDetailLocalDataSourceImpl
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.MovieDetailRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.services.MovieDetailKtorService
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.services.MovieDetailKtorServiceImpl
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.services.MovieVideosKtorService
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.services.MovieVideosKtorServiceImpl
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.remote.MovieDetailRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val movieDetailDataModule = module {
    single<MovieDetailKtorService> { MovieDetailKtorServiceImpl(get()) }
    single<MovieVideosKtorService> { MovieVideosKtorServiceImpl(get()) }
    singleOf(::MovieDetailLocalDataSourceImpl) bind MovieDetailLocalDataSource::class
    singleOf(::MovieDetailRemoteDataSourceImpl) bind MovieDetailRemoteDataSource::class
}
