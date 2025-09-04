package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.database.models.MovieDetailDataEntity
import com.msoula.hobbymatchmaker.core.database.models.MovieUpdatedDataEntity
import kotlinx.coroutines.flow.Flow

interface MovieDetailLocalDataSource {
    fun observeMovieDetail(movieId: Long): Flow<MovieDetailDataEntity?>
    suspend fun updateMovieVideoUri(
        movieId: Long,
        videoKey: String
    ): AppResult<Unit, AppError>

    suspend fun saveMovieDetail(movieDetail: MovieUpdatedDataEntity): AppResult<Unit, AppError>
}
