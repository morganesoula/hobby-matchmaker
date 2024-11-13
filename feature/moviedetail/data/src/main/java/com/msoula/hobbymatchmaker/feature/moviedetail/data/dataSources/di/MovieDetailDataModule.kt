package com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.di

import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.local.MovieDetailLocalDataSourceImpl
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.MovieDetailRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.services.MovieDetailService
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.services.MovieVideosService
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.dataSources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.dataSources.remote.MovieDetailRemoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit

val movieDetailDataModule = module {
    single<MovieDetailService> { get<Retrofit>().create(MovieDetailService::class.java) }
    single<MovieVideosService> { get<Retrofit>().create(MovieVideosService::class.java) }
    singleOf(::MovieDetailLocalDataSourceImpl) bind MovieDetailLocalDataSource::class
    singleOf(::MovieDetailRemoteDataSourceImpl) bind MovieDetailRemoteDataSource::class
}
