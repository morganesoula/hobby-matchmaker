package com.msoula.hobbymatchmaker.feature.moviedetail.domain.di

import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories.MovieDetailRepository
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases.FetchMovieDetailUseCase
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
    fun provideMovieDetailRepository(movieDetailRemoteDataSource: MovieDetailRemoteDataSource): MovieDetailRepository {
        return MovieDetailRepository(movieDetailRemoteDataSource)
    }

    @Provides
    fun provideFetchMovieDetailUseCase(movieDetailRepository: MovieDetailRepository): FetchMovieDetailUseCase {
        return FetchMovieDetailUseCase(movieDetailRepository)
    }
}
