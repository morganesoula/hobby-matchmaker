package com.msoula.hobbymatchmaker.features.movies.domain.data_sources

import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieLocalDataSource {
    fun observeMovies(): Flow<List<MovieDomainModel>>

    suspend fun deleteAllMovies()

    suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean,
    )

    suspend fun insertMovie(movie: MovieDomainModel)

    suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String
    )

    suspend fun upsertAll(movies: List<MovieDomainModel>)
}
