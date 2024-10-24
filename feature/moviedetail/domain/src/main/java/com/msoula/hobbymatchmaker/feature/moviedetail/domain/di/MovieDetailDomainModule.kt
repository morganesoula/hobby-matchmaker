package com.msoula.hobbymatchmaker.feature.moviedetail.domain.di

import com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories.MovieDetailRepository
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.FetchMovieDetailTrailerUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.FetchMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.useCases.UpdateMovieVideoURIUseCase
import org.koin.dsl.module

val movieDetailDomainModule = module {
    single<MovieDetailRepository> { MovieDetailRepository(get(), get()) }
    factory<FetchMovieDetailUseCase> { FetchMovieDetailUseCase(get()) }
    factory<FetchMovieDetailTrailerUseCase> { FetchMovieDetailTrailerUseCase(get()) }
    factory<UpdateMovieVideoURIUseCase> { UpdateMovieVideoURIUseCase(get()) }
    factory<ObserveMovieDetailUseCase> { ObserveMovieDetailUseCase(get(), get(), get()) }
    factory<ManageMovieTrailerUseCase> { ManageMovieTrailerUseCase(get(), get(), get()) }
}
