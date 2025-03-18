package com.msoula.hobbymatchmaker.features.movies.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.database.HMMLocalDatabase
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.mappers.toMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.mappers.toMovieEntityModel
import com.msoula.hobbymatchmaker.features.movies.domain.dataSources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class MovieLocalDataSourceImpl(private val database: HMMLocalDatabase) : MovieLocalDataSource {

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        return database.movieDao().observeMovies()
            .map { list ->
                list.map { movieEntity -> movieEntity.toMovieDomainModel() }
            }
    }

    override suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean,
    ) {
        try {
            database.movieDao().updateMovieFavorite(id, isFavorite)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Logger.e("Error updating movie", throwable = exception)
        }
    }

    override suspend fun insertMovie(movie: MovieDomainModel) {
        try {
            database.movieDao().upsertMovie(movie.toMovieEntityModel())
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Logger.e("Error on inserting movie", exception)
        }
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String
    ) {
        try {
            database.movieDao().updateMovieCover(coverFileName, localCoverFilePath)
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: Exception) {
            Logger.e("Error updating movie with local cover", throwable = e)
        }
    }

    override suspend fun upsertAll(movies: List<MovieDomainModel>) {
        try {
            database.movieDao().upsertMovies(movies.map { it.toMovieEntityModel() })
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: Exception) {
            Logger.e("Error upserting movies", e)
        }
    }
}
