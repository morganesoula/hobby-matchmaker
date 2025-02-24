package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import com.msoula.hobbymatchmaker.core.database.dao.MovieDAO
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.mappers.toMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.mappers.toMovieEntityModel
import com.msoula.hobbymatchmaker.features.movies.domain.dataSources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class MovieLocalDataSourceImpl
    (private val movieDAO: MovieDAO) : MovieLocalDataSource {

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return movieDAO.observeMovies()
            .map { list ->
                list.map { movieEntity -> movieEntity.toMovieDomainModel() }
            }
    }

    override suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean,
    ) {
        try {
            movieDAO.updateMovieFavorite(id, isFavorite)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            //Log.e("HMM", "Error updating movie: $exception")
            println("Error updating movie: $exception")
        }
    }

    override suspend fun insertMovie(movie: MovieDomainModel) {
        try {
            movieDAO.upsertMovie(movie.toMovieEntityModel())
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            //Log.e("HMM", "Error on inserting movie: $exception")
            println("Error on inserting movie: $exception")
        }
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String
    ) {
        try {
            movieDAO.updateMovieCover(coverFileName, localCoverFilePath)
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: Exception) {
            //Log.e("HMM", "Error updating movie with local cover: $e")
            println("Error updating movie with local cover: $e")
        }
    }

    override suspend fun upsertAll(movies: List<MovieDomainModel>) {
        try {
            movieDAO.upsertMovies(movies.map { it.toMovieEntityModel() })
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: Exception) {
            //Log.e("HMM", "Error upserting movies: $e")
            println("Error upserting movies: $e")
        }
    }
}
