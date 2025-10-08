package com.msoula.hobbymatchmaker.features.movies.data.di

import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.ImageRepositoryImpl
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.ImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val featuresModuleMovieDataPlatformSpecific = module {
    single<CoroutineDispatcher>(named("imageDispatcher")) { Dispatchers.IO }
    single<ImageRepository> {
        ImageRepositoryImpl(
            get(named("imageDispatcher")),
            get()
        )
    }
}
