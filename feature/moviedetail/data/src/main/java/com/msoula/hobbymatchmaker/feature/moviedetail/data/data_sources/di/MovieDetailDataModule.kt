package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.di

import com.msoula.hobbymatchmaker.core.database.dao.MovieDAO
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.local.MovieDetailLocalDataSourceImpl
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.MovieDetailRemoteDataSourceImpl
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.services.MovieDetailService
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.remote.MovieDetailRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MovieDetailDataModule {

    @Provides
    @Singleton
    fun provideMovieDetailService(retrofit: Retrofit): MovieDetailService {
        return retrofit.create(MovieDetailService::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieRemoteDetailDataSource(movieDetailService: MovieDetailService): MovieDetailRemoteDataSource {
        return MovieDetailRemoteDataSourceImpl(movieDetailService)
    }

    @Provides
    @Singleton
    fun provideMovieDetailRepository(movieDAO: MovieDAO): MovieDetailLocalDataSource {
        return MovieDetailLocalDataSourceImpl(movieDAO)
    }
}
