package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.safeCallStorage
import com.msoula.hobbymatchmaker.core.database.services.MovieDAO
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.mappers.toMovieDetailDataModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.mappers.toMovieUpdatedDetailDataEntity
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.models.MovieDetailDataModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.models.UpdatedMovieDetailDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieDetailLocalDataSourceImpl(
    private val movieDAO: MovieDAO
) : MovieDetailLocalDataSource {

    override fun observeMovieDetail(movieId: Long): Flow<MovieDetailDataModel?> =
        movieDAO.observeMovieWithActor(movieId).map { it.toMovieDetailDataModel() }

    override suspend fun updateMovieVideoUri(
        movieId: Long,
        videoKey: String
    ): AppResult<Unit, AppError> =
        safeCallStorage {
            movieDAO.updateMovieVideoKey(movieId, videoKey)
        }

    override suspend fun saveMovieDetail(updatedMovieDetail: UpdatedMovieDetailDataModel): AppResult<Unit, AppError> =
        safeCallStorage {
            movieDAO.updateExistingMovieWithDetail(updatedMovieDetail.toMovieUpdatedDetailDataEntity())
        }
}
