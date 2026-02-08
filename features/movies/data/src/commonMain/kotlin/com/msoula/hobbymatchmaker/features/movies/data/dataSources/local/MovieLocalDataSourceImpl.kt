package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeCallStorage
import com.msoula.hobbymatchmaker.core.database.services.MovieDAOImpl
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.mappers.toMovie
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.mappers.toMovieDataModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.local.models.MovieDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieLocalDataSourceImpl(private val movieDAO: MovieDAOImpl) : MovieLocalDataSource {

    override fun observeMovies(): Flow<List<MovieDataModel>> {
        return movieDAO.observeMovies().map { movies -> movies.map { it.toMovieDataModel() } }
    }

    override fun observeMoviesLikedCount(): Flow<Long> {
        return movieDAO.observeMoviesFavoriteCount()
    }

    override fun observeLikedMoviesIds(): Flow<List<Long>> {
        return movieDAO.observeLikedMoviesIds()
    }

    override suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError> = safeCallStorage {
        val isFavoriteDB = if (isFavorite) 1L else 0L
        movieDAO.updateMovieFavorite(id, isFavoriteDB)
    }

    override suspend fun insertMovie(movie: MovieDataModel): AppResult<Unit, AppError> =
        safeCallStorage {
            movieDAO.insertMovie(movie.toMovie())
        }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ): AppResult<Unit, AppError> = safeCallStorage {
        movieDAO.updateMovieCover(coverFileName, localCoverFilePath, movieId)
    }

    override suspend fun upsertAll(movies: List<MovieDataModel>): AppResult<Unit, AppError> =
        safeCallStorage {
            movieDAO.upsertMovies(movies.map { movie -> movie.toMovie() })
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
