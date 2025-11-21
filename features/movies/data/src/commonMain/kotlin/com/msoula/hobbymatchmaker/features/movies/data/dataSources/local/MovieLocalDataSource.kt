package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.database.Movie
import kotlinx.coroutines.flow.Flow

interface MovieLocalDataSource {
    fun observeMovies(): Flow<List<Movie>>
    fun observeMoviesLikedCount(): Flow<Long>
    suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError>

    suspend fun insertMovie(movie: Movie): AppResult<Unit, AppError>
    suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ): AppResult<Unit, AppError>

    suspend fun upsertAll(movies: List<Movie>): AppResult<Unit, AppError>
    suspend fun isMovieSynopsisAvailable(movieId: Long): AppResult<Boolean, AppError>
    suspend fun getFavoriteLocalMovieIds(): AppResult<List<Long>, AppError>
}
