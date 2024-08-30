package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.di

import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.local.MovieDetailLocalDataSourceImpl
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.MovieDetailRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.services.MovieDetailService
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.services.MovieVideosService
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.remote.MovieDetailRemoteDataSource
import org.koin.dsl.module
import retrofit2.Retrofit

val movieDetailDataModule = module {
    single<MovieDetailService> { get<Retrofit>().create(MovieDetailService::class.java) }
    single<MovieVideosService> { get<Retrofit>().create(MovieVideosService::class.java) }
    single<MovieDetailRemoteDataSource> { MovieDetailRemoteDataSourceImpl(get(), get()) }
    single<MovieDetailLocalDataSource> { MovieDetailLocalDataSourceImpl(get()) }
}
