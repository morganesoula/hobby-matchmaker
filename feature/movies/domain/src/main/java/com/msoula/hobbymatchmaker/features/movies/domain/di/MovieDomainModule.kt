package com.msoula.hobbymatchmaker.features.movies.domain.di

import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepositoryImpl
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.FetchMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.utils.ImageHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val movieDomainModule = module {
    single<MovieRepository> { MovieRepositoryImpl(get(), get()) }
    factory<ImageHelper> { ImageHelper(get(), androidContext()) }
    factory<SetMovieFavoriteUseCase> { SetMovieFavoriteUseCase(get()) }
    factory<FetchMoviesUseCase> { FetchMoviesUseCase(get()) }
    factory<ObserveAllMoviesUseCase> { ObserveAllMoviesUseCase(get(), get(), get()) }
}
