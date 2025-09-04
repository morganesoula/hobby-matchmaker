package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteModel

interface MovieRemoteDataSource {
    suspend fun fetchMovies(language: String): AppResult<List<MovieRemoteModel>, AppError>
    suspend fun updateUserFavoriteMovieList(uuidUser: String, movieId: Long, isFavorite: Boolean):
        AppResult<Unit, AppError>
    suspend fun setUserFavoriteMovies(uid: String, ids: List<Long>): AppResult<Unit, AppError>
}
