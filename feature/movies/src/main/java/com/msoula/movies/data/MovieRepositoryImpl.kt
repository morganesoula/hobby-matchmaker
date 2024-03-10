package com.msoula.movies.data

import android.util.Log
import com.msoula.database.data.dao.MovieDAO
import com.msoula.movies.data.mapper.MapMovieEntityToMovie
import com.msoula.movies.data.mapper.MapMovieToMovieEntity
import com.msoula.movies.data.model.Movie
import com.msoula.movies.domain.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty

class MovieRepositoryImpl(
    private val movieDAO: MovieDAO,
    private val movieService: MovieService,
    private val mapperMovieToEntity: MapMovieToMovieEntity,
    private val mapperEntityToMovie: MapMovieEntityToMovie,
) : MovieRepository {
    override fun observeMovies(): Flow<List<Movie>?> {
        Log.d("HMM", "Fetching movies in Repository")
        return movieService.observeMovies()
            .map { list ->
                Log.d("HMM", "List in Repository is $list")
                return@map mapperEntityToMovie.mapList(list)
            }
            .onEmpty {
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

    override suspend fun updateMovie(
        movieId: Int,
        isFavorite: Boolean,
    ) {
        try {
            movieService.updateMovie(movieId, isFavorite)
        } catch (exception: Exception) {
            exception.printStackTrace()
            Log.e("HMM", "Error updating movie: $exception")
        }
    }

    override suspend fun insertMovie(movie: Movie) {
        try {
            Log.i("HMM", "Into Repository - Inserting movie")
            movieDAO.insertMovie(mapperMovieToEntity.map(movie))
        } catch (exception: Exception) {
            exception.printStackTrace()
            Log.e("HMM", "Error on inserting movie: $exception")
        }
    }
}
