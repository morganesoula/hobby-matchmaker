package com.msoula.hobbymatchmaker.feature.moviedetail.domain.di

import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories.MovieDetailRepository
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.FetchMovieDetailUseCase
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.ObserveMovieDetailUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MovieDetailDomainModule {

    @Provides
    @Singleton
    fun provideMovieDetailRepository(
        movieDetailRemoteDataSource: MovieDetailRemoteDataSource,
        movieDetailLocalDataSource: MovieDetailLocalDataSource
    ): MovieDetailRepository {
        return MovieDetailRepository(movieDetailRemoteDataSource, movieDetailLocalDataSource)
    }

    @Provides
    fun provideFetchMovieDetailUseCase(movieDetailRepository: MovieDetailRepository): FetchMovieDetailUseCase {
        return FetchMovieDetailUseCase(movieDetailRepository)
    }

    @Provides
    fun provideGetMovieDetailUseCase(movieDetailRepository: MovieDetailRepository): ObserveMovieDetailUseCase {
        return ObserveMovieDetailUseCase(movieDetailRepository)
    }
}
