package com.msoula.movies.domain

import com.msoula.movies.data.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun observeMovies(): Flow<List<Movie>?>

    suspend fun deleteAllMovies()

    suspend fun updateMovie(
        movieId: Int,
        isFavorite: Boolean,
    )

    suspend fun insertMovie(movie: Movie)
}
