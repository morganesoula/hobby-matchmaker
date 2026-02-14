package com.msoula.hobbymatchmaker.features.movies.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.domain.models.FavoriteMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.models.PaginationInfoDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun observeMovies(): Flow<List<MovieDomainModel>>
    fun observeMoviesLikedCount(): Flow<Long>
    fun observeLikedMoviesIds(): Flow<List<Long>>
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

    suspend fun refreshMovies(language: String): AppResult<Unit, AppError>
    suspend fun loadMoreMovies(language: String, page: Int): AppResult<PaginationInfoDomainModel, AppError>
    suspend fun isSynopsisMovieAvailable(movieId: Long): AppResult<Boolean, AppError>
    suspend fun getFavoriteLocalMovieIds(): AppResult<List<Long>, AppError>
    suspend fun syncUserFavoritesRemote(
        uid: String,
        localIds: List<Long>
    ): AppResult<Unit, AppError>

    suspend fun getLastMovieSyncTimestamp(): Long
    fun observeFavoriteMovies(): Flow<List<FavoriteMovieDomainModel>>
}
