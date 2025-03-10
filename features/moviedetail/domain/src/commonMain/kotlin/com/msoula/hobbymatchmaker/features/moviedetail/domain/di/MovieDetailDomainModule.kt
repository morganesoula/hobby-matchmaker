package com.msoula.hobbymatchmaker.features.moviedetail.domain.di

import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepositoryImpl
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.FetchMovieDetailTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.FetchMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.UpdateMovieVideoURIUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featuresModuleMovieDetailDomain = module {
    singleOf(::MovieDetailRepositoryImpl) bind MovieDetailRepository::class
    factoryOf(::FetchMovieDetailUseCase)
    factoryOf(::FetchMovieDetailTrailerUseCase)
    factoryOf(::UpdateMovieVideoURIUseCase)
    factoryOf(::ObserveMovieDetailUseCase)
    factoryOf(::ManageMovieTrailerUseCase)
}
