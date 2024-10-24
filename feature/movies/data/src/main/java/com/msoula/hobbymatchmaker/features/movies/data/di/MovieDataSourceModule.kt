package com.msoula.hobbymatchmaker.features.movies.data.di

import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.MovieLocalDataSourceImpl
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.MovieRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services.TMDBService
import com.msoula.hobbymatchmaker.features.movies.domain.dataSources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.dataSources.MovieRemoteDataSource
import org.koin.dsl.module
import retrofit2.Retrofit

val movieDataModule = module {
    single<MovieRemoteDataSource> { MovieRemoteDataSourceImpl(get(), get(), get()) }
    single<MovieLocalDataSource> { MovieLocalDataSourceImpl(get()) }
    single<TMDBService> { get<Retrofit>().create(TMDBService::class.java) }
}
