package com.msoula.hobbymatchmaker.features.movies.data.di

import com.msoula.hobbymatchmaker.core.database.dao.MovieDAO
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.local.MovieLocalDataSourceImpl
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.MovieRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.services.TMDBService
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.utils.ImageHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MovieDataSourceModule {

    @Provides
    @Singleton
    fun provideMovieLocalDataSource(movieDAO: MovieDAO): MovieLocalDataSource =
        MovieLocalDataSourceImpl(movieDAO)

    @Provides
    @Singleton
    fun provideMovieRemoteDataSource(
        tmdbService: TMDBService,
        imageHelper: ImageHelper
    ): MovieRemoteDataSource =
        MovieRemoteDataSourceImpl(tmdbService, imageHelper)

    @Provides
    @Singleton
    fun provideTMDBService(retrofit: Retrofit): TMDBService {
        return retrofit.create(TMDBService::class.java)
    }
}
