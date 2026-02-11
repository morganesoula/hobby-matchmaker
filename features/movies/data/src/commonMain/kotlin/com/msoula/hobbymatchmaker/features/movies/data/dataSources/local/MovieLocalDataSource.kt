package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.models.MovieLocalDataModel
import kotlinx.coroutines.flow.Flow

interface MovieLocalDataSource {
    fun observeMovies(): Flow<List<MovieLocalDataModel>>
    fun observeMoviesLikedCount(): Flow<Long>
    fun observeLikedMoviesIds(): Flow<List<Long>>
    suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError>

    suspend fun insertMovie(movie: MovieLocalDataModel): AppResult<Unit, AppError>
    suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ): AppResult<Unit, AppError>

    suspend fun upsertAll(movies: List<MovieLocalDataModel>): AppResult<Unit, AppError>
    suspend fun isMovieSynopsisAvailable(movieId: Long): AppResult<Boolean, AppError>
    suspend fun getFavoriteLocalMovieIds(): AppResult<List<Long>, AppError>
}
