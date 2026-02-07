package com.msoula.hobbymatchmaker.features.movies.domain.di

import com.msoula.hobbymatchmaker.features.movies.domain.useCases.CheckMovieSynopsisValueUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.LoadMoreMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveLikedMoviesCountUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveLikedMoviesIdsUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.RefreshMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ShouldRefreshMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SyncLocalFavoritesToCloudUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleMovieDomain = module {
    factoryOf(::SetMovieFavoriteUseCase)
    factoryOf(::RefreshMoviesUseCase)
    factoryOf(::ObserveAllMoviesUseCase)
    factoryOf(::CheckMovieSynopsisValueUseCase)
    factoryOf(::SyncLocalFavoritesToCloudUseCase)
    factoryOf(::LoadMoreMoviesUseCase)
    factoryOf(::ShouldRefreshMoviesUseCase)
    factoryOf(::ObserveLikedMoviesIdsUseCase)
    factoryOf(::ObserveLikedMoviesCountUseCase)
}
