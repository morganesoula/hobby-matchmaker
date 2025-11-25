package com.msoula.hobbymatchmaker.features.movies.domain.di

import com.msoula.hobbymatchmaker.features.movies.domain.useCases.CheckMovieSynopsisValueUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.FetchMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SyncLocalFavoritesToCloudUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val featuresModuleMovieDomain = module {
    factoryOf(::SetMovieFavoriteUseCase)
    factoryOf(::FetchMoviesUseCase)
    factoryOf(::ObserveAllMoviesUseCase)
    factoryOf(::CheckMovieSynopsisValueUseCase)
    factoryOf(::SyncLocalFavoritesToCloudUseCase)
}
