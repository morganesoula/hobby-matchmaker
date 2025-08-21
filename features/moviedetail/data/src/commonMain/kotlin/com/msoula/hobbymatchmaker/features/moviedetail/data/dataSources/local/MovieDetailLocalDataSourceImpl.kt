package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.core.common.safeCallStorage
import com.msoula.hobbymatchmaker.core.database.models.MovieDetailDataEntity
import com.msoula.hobbymatchmaker.core.database.models.MovieUpdatedDataEntity
import com.msoula.hobbymatchmaker.core.database.services.MovieDAOImpl
import kotlinx.coroutines.flow.Flow

class MovieDetailLocalDataSourceImpl(
    private val movieDAO: MovieDAOImpl
) : MovieDetailLocalDataSource {

    override fun observeMovieDetail(movieId: Long): Flow<MovieDetailDataEntity?> =
        movieDAO.observeMovieWithActor(movieId)

    override suspend fun updateMovieVideoUri(movieId: Long, videoKey: String): R<Unit, AppError> =
        safeCallStorage {
            movieDAO.updateMovieVideoKey(movieId, videoKey)
        }


    override suspend fun saveMovieDetail(movieDetail: MovieUpdatedDataEntity): R<Unit, AppError> =
        safeCallStorage {
            movieDAO.updateExistingMovieWithDetail(movieDetail)
        }

}
