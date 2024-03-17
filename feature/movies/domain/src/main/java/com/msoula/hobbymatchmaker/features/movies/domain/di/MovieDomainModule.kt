package com.msoula.hobbymatchmaker.features.movies.domain.di

import android.content.Context
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.DeleteAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.InsertMovieUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.use_cases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.utils.ImageHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MovieDomainModule {

    @Provides
    @Singleton
    fun provideMovieRepository(
        movieLocalDataSource: MovieLocalDataSource,
        movieRemoteDataSource: MovieRemoteDataSource
    ): MovieRepository = MovieRepository(movieLocalDataSource, movieRemoteDataSource)

    @Provides
    fun provideImageHelper(
        @ApplicationContext context: Context,
        coroutineDispatcher: CoroutineDispatcher,
    ): ImageHelper = ImageHelper(coroutineDispatcher, context)

    @Provides
    fun provideGetMovieByPopularityUseCase(movieRepository: MovieRepository): ObserveAllMoviesUseCase =
        ObserveAllMoviesUseCase(movieRepository)

    @Provides
    fun provideDeleteAllMoviesUseCase(movieRepository: MovieRepository): DeleteAllMoviesUseCase =
        DeleteAllMoviesUseCase(movieRepository)

    @Provides
    fun provideUpdateMovieUseCase(movieRepository: MovieRepository): SetMovieFavoriteUseCase =
        SetMovieFavoriteUseCase(movieRepository)

    @Provides
    fun provideInsertMovieUseCase(movieRepository: MovieRepository): InsertMovieUseCase =
        InsertMovieUseCase(movieRepository)
}
