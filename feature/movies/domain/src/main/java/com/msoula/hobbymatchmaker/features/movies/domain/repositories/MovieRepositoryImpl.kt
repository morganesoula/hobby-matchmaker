package com.msoula.hobbymatchmaker.features.movies.domain.repositories

import android.util.Log
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow

class MovieRepositoryImpl(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieLocalDataSource: MovieLocalDataSource
) : MovieRepository {

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return movieLocalDataSource.observeMovies()
    }

    override suspend fun updateMovieWithFavoriteValue(
        uuidUser: String,
        id: Long,
        isFavorite: Boolean
    ) {
        Log.d("HMM", "repository favorite value: $id, $isFavorite")
        movieLocalDataSource.updateMovieWithFavoriteValue(id, isFavorite)
        movieRemoteDataSource.updateUserFavoriteMovieList(uuidUser, id, isFavorite)
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String
    ) {
        movieLocalDataSource.updateMovieWithLocalCoverFilePath(coverFileName, localCoverFilePath)
    }

    override suspend fun fetchMovies(language: String): Result<Unit> =
        movieRemoteDataSource.fetchMovies(language)
            .mapSuccess { movies ->
                movieLocalDataSource.upsertAll(movies)
                Result.Success(Unit)
            }
}
