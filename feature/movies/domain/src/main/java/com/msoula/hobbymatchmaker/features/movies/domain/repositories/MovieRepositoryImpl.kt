package com.msoula.hobbymatchmaker.features.movies.domain.repositories

import android.util.Log
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow

class MovieRepositoryImpl(
    private val movieRemoteDataSource: MovieRemoteDataSource
) : MovieRepository {

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return movieRemoteDataSource.observeMovies()
    }

    override suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean
    ) {
        Log.d("HMM", "repository favorite value: $id, $isFavorite")
        movieRemoteDataSource.setMovieFavoriteValue(id, isFavorite)
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String
    ) {
        movieRemoteDataSource.setMovieWithLocalCoverFilePath(coverFileName, localCoverFilePath)
    }

    override suspend fun fetchMovies(language: String): Result<Unit> =
        movieRemoteDataSource.fetchMovies(language)
            .mapSuccess { movies ->
                movieRemoteDataSource.upsertMovies(movies)
                Result.Success(Unit)
            }
}
