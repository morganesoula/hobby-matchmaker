package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteModel

interface MovieRemoteDataSource {
    suspend fun fetchMovies(language: String): R<List<MovieRemoteModel>, AppError>
    suspend fun updateUserFavoriteMovieList(uuidUser: String, movieId: Long, isFavorite: Boolean):
        R<Unit, AppError>

    suspend fun setUserFavoriteMovies(uid: String, ids: List<Long>): R<Unit, AppError>
}
