package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteModel
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.PaginatedMovieResult

interface MovieRemoteDataSource {
    suspend fun refreshMovies(language: String): AppResult<List<MovieRemoteModel>, AppError>
    suspend fun fetchMoviesPage(
        language: String,
        page: Int
    ): AppResult<PaginatedMovieResult, AppError>

    suspend fun updateUserFavoriteMovieList(uuidUser: String, movieId: Long, isFavorite: Boolean):
        AppResult<Unit, AppError>

    suspend fun setUserFavoriteMovies(uid: String, ids: List<Long>): AppResult<Unit, AppError>
}
