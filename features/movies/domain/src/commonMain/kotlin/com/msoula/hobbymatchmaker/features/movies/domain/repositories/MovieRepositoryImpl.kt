package com.msoula.hobbymatchmaker.features.movies.domain.repositories

import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.dataSources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.dataSources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.errors.MovieErrors
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

class MovieRepositoryImpl(
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val movieLocalDataSource: MovieLocalDataSource
) : MovieRepository {

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return movieLocalDataSource.observeMovies()
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ) {
        movieLocalDataSource.updateMovieWithLocalCoverFilePath(
            coverFileName,
            localCoverFilePath,
            movieId
        )
    }

    override suspend fun fetchMovies(language: String): Result<Unit, MovieErrors> {
        val result = movieRemoteDataSource.fetchMovies(language)
        return result
            .mapSuccess { movies ->
                movieLocalDataSource.upsertAll(movies)
                Result.Success(Unit)
            }
    }

    override suspend fun updateMovieFavoriteLocal(id: Long, isFavorite: Boolean) {
        movieLocalDataSource.updateMovieWithFavoriteValue(id, isFavorite)
    }

    override suspend fun updateMovieFavoriteRemote(uid: String, id: Long, isFavorite: Boolean) {
        try {
            movieRemoteDataSource.updateUserFavoriteMovieList(uid, id, isFavorite)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e("Could not update movie $id remotely")
        }
    }

    override suspend fun isSynopsisMovieAvailable(movieId: Long): Boolean {
        return movieLocalDataSource.isMovieSynopsisAvailable(movieId)
    }

    override suspend fun getFavoriteLocalMovieIds(): List<Long> =
        movieLocalDataSource.getFavoriteLocalMovieIds()

    override suspend fun syncUserFavoritesRemote(uid: String, localIds: List<Long>) {
        try {
            movieRemoteDataSource.setUserFavoriteMovies(uid, localIds)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.w(
                "Favorite remote movie push failed, will sync at next login - ${e.message}"
            )
        }
    }
}
