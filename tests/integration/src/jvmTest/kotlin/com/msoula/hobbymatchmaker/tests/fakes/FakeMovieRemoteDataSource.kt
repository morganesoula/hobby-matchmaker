package com.msoula.hobbymatchmaker.tests.fakes

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.MovieRemoteDataSource
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieRemoteModel

class FakeMovieRemoteDataSource(
    private val pages: List<List<MovieRemoteModel>> = emptyList(),
    private val failOnFetch: AppError? = null
) : MovieRemoteDataSource {
    override suspend fun fetchMovies(language: String): AppResult<List<MovieRemoteModel>, AppError> {
        failOnFetch?.let { return AppResult.Failure(it) }
        return AppResult.Success(pages.flatten())
    }

    override suspend fun updateUserFavoriteMovieList(
        uuidUser: String,
        movieId: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError> = AppResult.Success(Unit)

    override suspend fun setUserFavoriteMovies(
        uid: String,
        ids: List<Long>
    ): AppResult<Unit, AppError> = AppResult.Success(Unit)
}
