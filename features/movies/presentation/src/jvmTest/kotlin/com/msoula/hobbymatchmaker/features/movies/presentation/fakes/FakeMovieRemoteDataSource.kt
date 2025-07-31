package com.msoula.hobbymatchmaker.features.movies.presentation.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.dataSources.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.domain.errors.MovieErrors
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeMovieRemoteDataSource(
    private val fetchMoviesResult: Result<List<MovieDomainModel>, MovieErrors> =
        Result.Success(emptyList())
) : MovieRemoteDataSource {
    private val movies = MutableStateFlow<List<MovieDomainModel>>(emptyList())
    val currentMovie = MutableStateFlow(MovieDomainModel())

    override suspend fun fetchMovies(language: String):
        Result<List<MovieDomainModel>, MovieErrors> = fetchMoviesResult

    override suspend fun updateUserFavoriteMovieList(
        uuidUser: String,
        movieId: Long,
        isFavorite: Boolean
    ) {
        currentMovie.update {
            it.copy(
                isFavorite = isFavorite
            )
        }
    }
}
