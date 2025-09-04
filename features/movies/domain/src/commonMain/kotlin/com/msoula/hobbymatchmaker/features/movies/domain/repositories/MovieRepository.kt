package com.msoula.hobbymatchmaker.features.movies.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun observeMovies(): Flow<List<MovieDomainModel>>

    suspend fun updateMovieFavoriteLocal(id: Long, isFavorite: Boolean): AppResult<Unit, AppError>
    suspend fun updateMovieFavoriteRemote(
        uid: String,
        id: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError>

    suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ): AppResult<Unit, AppError>

    suspend fun fetchMovies(language: String): AppResult<Unit, AppError>

    suspend fun isSynopsisMovieAvailable(movieId: Long): AppResult<Boolean, AppError>
    suspend fun getFavoriteLocalMovieIds(): AppResult<List<Long>, AppError>

    suspend fun syncUserFavoritesRemote(uid: String, localIds: List<Long>): AppResult<Unit, AppError>
}
