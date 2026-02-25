package com.msoula.hobbymatchmaker.features.hub.domain.di

import com.msoula.hobbymatchmaker.features.hub.domain.useCases.ObserveMatchedFriendsUseCase
import com.msoula.hobbymatchmaker.features.hub.domain.useCases.ObserveSharedFavoriteMoviesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val featuresModuleHubDomain = module {
    factoryOf(::ObserveMatchedFriendsUseCase)
    factoryOf(::ObserveSharedFavoriteMoviesUseCase)
}
