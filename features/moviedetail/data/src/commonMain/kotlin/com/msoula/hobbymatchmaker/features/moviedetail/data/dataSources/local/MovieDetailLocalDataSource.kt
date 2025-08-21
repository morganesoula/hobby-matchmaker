package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.core.database.models.MovieDetailDataEntity
import com.msoula.hobbymatchmaker.core.database.models.MovieUpdatedDataEntity
import kotlinx.coroutines.flow.Flow

interface MovieDetailLocalDataSource {
    fun observeMovieDetail(movieId: Long): Flow<MovieDetailDataEntity?>
    suspend fun updateMovieVideoUri(
        movieId: Long,
        videoKey: String
    ): R<Unit, AppError>

    suspend fun saveMovieDetail(movieDetail: MovieUpdatedDataEntity): R<Unit, AppError>
}
