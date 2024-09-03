package com.msoula.hobbymatchmaker.features.movies.data.di

import com.msoula.hobbymatchmaker.features.movies.data.data_sources.local.MovieLocalDataSourceImpl
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.MovieRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.services.TMDBService
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import org.koin.dsl.module
import retrofit2.Retrofit

val movieDataModule = module {
    single<MovieLocalDataSource> { MovieLocalDataSourceImpl(get()) }
    single<MovieRemoteDataSource> { MovieRemoteDataSourceImpl(get(), get()) }
    single<TMDBService> { get<Retrofit>().create(TMDBService::class.java) }
}
