package com.msoula.hobbymatchmaker.feature.moviedetail.domain.di

import com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories.MovieDetailRepository
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories.MovieDetailRepositoryImpl
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.FetchMovieDetailTrailerUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.FetchMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.UpdateMovieVideoURIUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val movieDetailDomainModule = module {
    singleOf(::MovieDetailRepositoryImpl) bind MovieDetailRepository::class
    factoryOf(::FetchMovieDetailUseCase)
    factoryOf(::FetchMovieDetailTrailerUseCase)
    factoryOf(::UpdateMovieVideoURIUseCase)
    factoryOf(::ObserveMovieDetailUseCase)
    factoryOf(::ManageMovieTrailerUseCase)
}
