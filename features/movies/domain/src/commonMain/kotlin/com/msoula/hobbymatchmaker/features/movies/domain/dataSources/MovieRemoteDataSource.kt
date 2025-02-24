package com.msoula.hobbymatchmaker.features.movies.domain.dataSources

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.errors.MovieErrors
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel

interface MovieRemoteDataSource {
    suspend fun fetchMovies(language: String): Result<List<MovieDomainModel>, MovieErrors>
    suspend fun updateUserFavoriteMovieList(uuidUser: String, movieId: Long, isFavorite: Boolean)
}
