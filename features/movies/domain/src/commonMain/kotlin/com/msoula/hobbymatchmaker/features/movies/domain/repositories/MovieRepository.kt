package com.msoula.hobbymatchmaker.features.movies.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun observeMovies(): Flow<List<MovieDomainModel>>

    suspend fun updateMovieFavoriteLocal(id: Long, isFavorite: Boolean): R<Unit, AppError>
    suspend fun updateMovieFavoriteRemote(
        uid: String,
        id: Long,
        isFavorite: Boolean
    ): R<Unit, AppError>

    suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ): R<Unit, AppError>

    suspend fun fetchMovies(language: String): R<Unit, AppError>

    suspend fun isSynopsisMovieAvailable(movieId: Long): R<Boolean, AppError>
    suspend fun getFavoriteLocalMovieIds(): R<List<Long>, AppError>

    suspend fun syncUserFavoritesRemote(uid: String, localIds: List<Long>)
}
