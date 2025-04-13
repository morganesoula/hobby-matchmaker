package com.msoula.hobbymatchmaker.features.movies.domain.repositories

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.errors.MovieErrors
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun observeMovies(): Flow<List<MovieDomainModel>>

    suspend fun updateMovieWithFavoriteValue(
        uuidUser: String,
        id: Long,
        isFavorite: Boolean
    )

    suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    )

    suspend fun fetchMovies(language: String): Result<Unit, MovieErrors>

    suspend fun isSynopsisMovieAvailable(movieId: Long): Boolean
}
