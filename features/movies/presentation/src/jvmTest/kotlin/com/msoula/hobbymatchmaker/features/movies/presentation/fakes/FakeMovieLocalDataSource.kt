package com.msoula.hobbymatchmaker.features.movies.presentation.fakes

import com.msoula.hobbymatchmaker.features.movies.domain.dataSources.MovieLocalDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

class FakeMovieLocalDataSource : MovieLocalDataSource {
    private val moviesFlow = MutableStateFlow<List<MovieDomainModel>>(emptyList())
    override fun observeMovies(): Flow<List<MovieDomainModel>> = moviesFlow

    override suspend fun updateMovieWithFavoriteValue(id: Long, isFavorite: Boolean) {
        moviesFlow.value = moviesFlow.value.map { movie ->
            if (movie.id == id) movie.copy(isFavorite = isFavorite)
            else movie
        }
    }

    override suspend fun insertMovie(movie: MovieDomainModel) =
        moviesFlow.update { currentList -> currentList + movie }

    override suspend fun updateMovieWithLocalCoverFilePath(
        coverFileName: String,
        localCoverFilePath: String,
        movieId: Long
    ) {
        moviesFlow.update { list ->
            list.map { movie ->
                if (movie.id == movieId) movie.copy(localCoverFilePath = localCoverFilePath)
                else movie
            }
        }
    }

    override suspend fun upsertAll(movies: List<MovieDomainModel>) {
        moviesFlow.update { movies }
    }

    override suspend fun isMovieSynopsisAvailable(movieId: Long): Boolean {
        val currentList = moviesFlow.first()
        val movie = currentList.find { it.id == movieId }

        return !movie?.overview.isNullOrBlank()
    }
}
