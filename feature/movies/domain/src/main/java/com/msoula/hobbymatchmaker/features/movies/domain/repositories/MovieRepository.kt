package com.msoula.hobbymatchmaker.features.movies.domain.repositories

import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalCoroutinesApi::class)
class MovieRepository(
    private val movieLocalDataSource: MovieLocalDataSource,
    private val movieRemoteDataSource: MovieRemoteDataSource
) {

    fun observeMovies(): Flow<List<MovieDomainModel>> {
        return movieLocalDataSource.observeMovies()
            .flatMapConcat { list ->
                if (list.isEmpty()) {
                    movieRemoteDataSource.fetchMovies()
                    flow { emit(emptyList()) }
                } else {
                    flow { emit(list) }
                }
            }
    }

    suspend fun deleteAllMovies() {
        movieLocalDataSource.deleteAllMovies()
    }

    suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean,
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
}
