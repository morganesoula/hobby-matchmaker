package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.models.MovieDetailDataModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.models.UpdatedMovieDetailDataModel
import kotlinx.coroutines.flow.Flow

interface MovieDetailLocalDataSource {
    fun observeMovieDetail(movieId: Long): Flow<MovieDetailDataModel?>
    suspend fun updateMovieVideoUri(
        movieId: Long,
        videoKey: String
    ): AppResult<Unit, AppError>

    suspend fun saveMovieDetail(updatedMovieDetail: UpdatedMovieDetailDataModel): AppResult<Unit, AppError>
}
