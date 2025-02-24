package com.msoula.hobbymatchmaker.features.movies.data.dataSources.di

import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.ImageRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

//TODO Bind ImageRepository
val iosMovieLocalDataSourceModule = module {
    singleOf(::ImageRepositoryImpl)
}

