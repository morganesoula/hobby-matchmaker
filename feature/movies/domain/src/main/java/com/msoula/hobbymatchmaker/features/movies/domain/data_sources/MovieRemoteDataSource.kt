package com.msoula.hobbymatchmaker.features.movies.domain.data_sources

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieRemoteDataSource {
    suspend fun fetchMovies(language: String): Result<List<MovieDomainModel>>
    suspend fun upsertMovies(movies: List<MovieDomainModel>)
    fun observeMovies(): Flow<List<MovieDomainModel>>
    suspend fun setMovieFavoriteValue(movieId: Long, isFavorite: Boolean)
    suspend fun setMovieWithLocalCoverFilePath(coverFileName: String, localCoverFilePath: String)
}
