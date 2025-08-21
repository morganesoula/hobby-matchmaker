package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.core.database.Movie
import kotlinx.coroutines.flow.Flow

interface MovieLocalDataSource {
    fun observeMovies(): Flow<List<Movie>>
    suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean
    ): R<Unit, AppError>
    suspend fun insertMovie(movie: Movie): R<Unit, AppError>
    suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ): R<Unit, AppError>
    suspend fun upsertAll(movies: List<Movie>): R<Unit, AppError>
    suspend fun isMovieSynopsisAvailable(movieId: Long): R<Boolean, AppError>
    suspend fun getFavoriteLocalMovieIds(): R<List<Long>, AppError>
}
