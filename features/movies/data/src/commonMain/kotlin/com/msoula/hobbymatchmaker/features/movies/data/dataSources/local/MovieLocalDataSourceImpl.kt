package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.database.services.MovieDAOImpl
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.mappers.toMovieDB
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.mappers.toMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.dataSources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class MovieLocalDataSourceImpl(private val movieDAO: MovieDAOImpl) : MovieLocalDataSource {

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return movieDAO.observeMovies()
            .map { list ->
                list.map { movieEntity -> movieEntity.toMovieDomainModel() }
            }
    }

    override suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean
    ) {
        try {
            val isFavoriteDB = if (isFavorite) 1 else 0
            movieDAO.updateMovieFavorite(id, isFavoriteDB.toLong())
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Logger.e("Error updating movie", throwable = exception)
        }
    }

    override suspend fun insertMovie(movie: MovieDomainModel) {
        try {
            movieDAO.insertMovie(movie.toMovieDB())
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Logger.e("Error on inserting movie", exception)
        }
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ) {
        try {
            movieDAO.updateMovieCover(coverFileName, localCoverFilePath, movieId)
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: Exception) {
            Logger.e("Error updating movie with local cover", throwable = e)
        }
    }

    override suspend fun upsertAll(movies: List<MovieDomainModel>) {
        try {
            Logger.d("Local data source upsertAll")
            movieDAO.upsertMovies(movies.map { it.toMovieDB() })
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: Exception) {
            Logger.e("Error inserting movies", e)
        }
    }
}
