package com.msoula.hobbymatchmaker.features.movies.data.data_sources.local

import android.util.Log
import com.msoula.hobbymatchmaker.core.dao.MovieDAO
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.local.mappers.toMovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.data.data_sources.local.mappers.toMovieEntityModel
import com.msoula.hobbymatchmaker.features.movies.domain.data_sources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty

class MovieLocalDataSourceImpl(
    private val movieDAO: MovieDAO
) : MovieLocalDataSource {

    override fun observeMovies(): Flow<List<MovieDomainModel>> {
        Log.d("HMM", "Into MovieLocalDataSourceImpl")
        return movieDAO.observeMovies()
            .map { list ->
                Log.d("HMM", "Into MovieLocalDataSourceImpl with list: $list")
                list.map { movieEntity -> movieEntity.toMovieDomainModel() }
            }
            .onEmpty {
                Log.d("HMM", "Into MovieLocalDataSourceImpl and it's empty")
                return@onEmpty
            }
    }

    override suspend fun deleteAllMovies() {
        try {
            movieDAO.clearAll()
        } catch (exception: Exception) {
            exception.printStackTrace()
            Log.e("HMM", "Error deleting all movies")
        }
    }

    override suspend fun updateMovieWithFavoriteValue(
        id: Long,
        isFavorite: Boolean,
    ) {
        try {
            movieDAO.updateMovieFavorite(id, isFavorite)
        } catch (exception: Exception) {
            exception.printStackTrace()
            Log.e("HMM", "Error updating movie: $exception")
        }
    }

    override suspend fun insertMovie(movie: MovieDomainModel) {
        try {
            Log.i("HMM", "Into Repository - Inserting movie")
            movieDAO.insertMovie(movie.toMovieEntityModel())
        } catch (exception: Exception) {
            exception.printStackTrace()
            Log.e("HMM", "Error on inserting movie: $exception")
        }
    }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String
    ) {
        movieDAO.updateMovieWithLocalCoverFilePath(coverFileName, localCoverFilePath)
    }

    override suspend fun upsertAll(movies: List<MovieDomainModel>) {
        movieDAO.upsertAll(movies.map { it.toMovieEntityModel() })
    }
}
