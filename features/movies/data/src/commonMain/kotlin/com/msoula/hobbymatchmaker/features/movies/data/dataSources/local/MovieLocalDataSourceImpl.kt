package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeCallStorage
import com.msoula.hobbymatchmaker.core.database.Movie
import com.msoula.hobbymatchmaker.core.database.services.MovieDAOImpl
import kotlinx.coroutines.flow.Flow

class MovieLocalDataSourceImpl(private val movieDAO: MovieDAOImpl) : MovieLocalDataSource {

    override fun observeMovies(): Flow<List<Movie>> {
        return movieDAO.observeMovies()
    }

    override fun observeMoviesLikedCount(): Flow<Long> {
        return movieDAO.observeMoviesFavoriteCount()
    }

    override suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError> = safeCallStorage {
        val isFavoriteDB = if (isFavorite) 1L else 0L
        movieDAO.updateMovieFavorite(id, isFavoriteDB)
    }

    override suspend fun insertMovie(movie: Movie): AppResult<Unit, AppError> = safeCallStorage {
        movieDAO.insertMovie(movie)
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ): AppResult<Unit, AppError> = safeCallStorage {
        movieDAO.updateMovieCover(coverFileName, localCoverFilePath, movieId)
    }

    override suspend fun upsertAll(movies: List<Movie>): AppResult<Unit, AppError> =
        safeCallStorage {
            movieDAO.upsertMovies(movies)
        }

    override suspend fun isMovieSynopsisAvailable(movieId: Long): AppResult<Boolean, AppError> =
        safeCallStorage {
            movieDAO.isMovieSynopsisAvailable(movieId)
        }

    override suspend fun getFavoriteLocalMovieIds(): AppResult<List<Long>, AppError> =
        safeCallStorage {
            movieDAO.getFavoriteLocalMovieIds()
        }
}
