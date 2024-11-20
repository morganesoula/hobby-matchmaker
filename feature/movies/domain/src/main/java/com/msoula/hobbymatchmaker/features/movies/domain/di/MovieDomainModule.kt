package com.msoula.hobbymatchmaker.features.movies.domain.di

import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepositoryImpl
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.FetchMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.utils.ImageHelper
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val movieDomainModule = module {
    singleOf(::MovieRepositoryImpl) bind MovieRepository::class
    factoryOf(::ImageHelper)
    //factory<ImageHelper> { ImageHelper(get(), androidContext()) }
    factoryOf(::SetMovieFavoriteUseCase)
    factoryOf(::FetchMoviesUseCase)
    factoryOf(::ObserveAllMoviesUseCase)
}
