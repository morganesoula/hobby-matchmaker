package com.msoula.hobbymatchmaker.features.moviedetail.domain.di

import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.FetchMovieTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.SyncMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.UpdateMovieVideoURIUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleMovieDetailDomain = module {
    factoryOf(::UpdateMovieVideoURIUseCase)
    factoryOf(::ObserveMovieDetailUseCase)
    factoryOf(::FetchMovieTrailerUseCase)
    factoryOf(::SyncMovieDetailUseCase)
}
