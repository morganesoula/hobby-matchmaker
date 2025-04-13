package com.msoula.hobbymatchmaker.features.movies.domain.dataSources

import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieLocalDataSource {
    fun observeMovies(): Flow<List<MovieDomainModel>>
    suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean,
    )
    suspend fun insertMovie(movie: MovieDomainModel)
    suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    )
    suspend fun upsertAll(movies: List<MovieDomainModel>)
    suspend fun isMovieSynopsisAvailable(movieId: Long): Boolean
}
