package com.msoula.hobbymatchmaker.feature.moviedetail.domain.di

import com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories.MovieDetailRepository
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.FetchMovieDetailTrailerUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.FetchMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.UpdateMovieVideoURIUseCase
import org.koin.dsl.module

val movieDetailDomainModule = module {
    single<MovieDetailRepository> { MovieDetailRepository(get(), get()) }
    factory<FetchMovieDetailUseCase> { FetchMovieDetailUseCase(get()) }
    factory<ObserveMovieDetailUseCase> { ObserveMovieDetailUseCase(get()) }
    factory<FetchMovieDetailTrailerUseCase> { FetchMovieDetailTrailerUseCase(get()) }
    factory<UpdateMovieVideoURIUseCase> { UpdateMovieVideoURIUseCase(get()) }
}
