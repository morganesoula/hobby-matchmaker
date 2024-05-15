package com.msoula.hobbymatchmaker.features.movies.domain.repositories

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow

class MovieRepository(
    private val movieLocalDataSource: MovieLocalDataSource,
    private val movieRemoteDataSource: MovieRemoteDataSource
) {

    fun observeMovies(): Flow<List<MovieDomainModel>> {
        return movieLocalDataSource.observeMovies()
    }

    suspend fun deleteAllMovies() {
        movieLocalDataSource.deleteAllMovies()
    }

    suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean
    ) {
        movieLocalDataSource.updateMovieWithFavoriteValue(id, isFavorite)
    }

    suspend fun insertMovie(movie: MovieDomainModel) {
        movieLocalDataSource.insertMovie(movie)
    }

    suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String
    ) {
        movieLocalDataSource.updateMovieWithLocalCoverFilePath(coverFileName, localCoverFilePath)
    }

    suspend fun fetchMovies(): Result<Unit> =
        movieRemoteDataSource.fetchMovies()
            .mapSuccess { movies ->
                movieLocalDataSource.upsertAll(movies)
                Result.Success(Unit)
            }
}
