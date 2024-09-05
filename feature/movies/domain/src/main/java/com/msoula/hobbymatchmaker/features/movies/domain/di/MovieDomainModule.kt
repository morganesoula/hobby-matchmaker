package com.msoula.hobbymatchmaker.features.movies.domain.di

import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.FetchMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.utils.ImageHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val movieDomainModule = module {
    single<MovieRepository> { MovieRepository(get()) }
    factory<ImageHelper> { ImageHelper(get(), androidContext()) }
    factory<ObserveAllMoviesUseCase> { ObserveAllMoviesUseCase(get()) }
    factory<SetMovieFavoriteUseCase> { SetMovieFavoriteUseCase(get()) }
    factory<FetchMoviesUseCase> { FetchMoviesUseCase(get()) }
}
