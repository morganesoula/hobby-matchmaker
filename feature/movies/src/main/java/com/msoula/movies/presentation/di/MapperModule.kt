package com.msoula.movies.presentation.di

import com.msoula.movies.data.mapper.MapMovieEntityToMovie
import com.msoula.movies.data.mapper.MapMoviePogoToMovieEntity
import com.msoula.movies.data.mapper.MapMovieToMovieEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Provides
    fun providesMapEntityToMovieInfo(): MapMovieEntityToMovie = MapMovieEntityToMovie()

    @Provides
    fun providesMapPogoToMovieEntity(): MapMoviePogoToMovieEntity = MapMoviePogoToMovieEntity()

    @Provides
    fun providesMapMovieToMovieEntity(): MapMovieToMovieEntity = MapMovieToMovieEntity()
}
