package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local

import com.msoula.hobbymatchmaker.core.database.services.MovieDAOImpl
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.mappers.toMovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.mappers.toMovieUpdated
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieDetailLocalDataSourceImpl(
    private val movieDAO: MovieDAOImpl
) : MovieDetailLocalDataSource {

    override suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> {
        return movieDAO.observeMovieWithActor(movieId).map {
            it.toMovieDetailDomainModel()
        }
    }

    override suspend fun updateMovieVideoUri(movieId: Long, videoKey: String) {
        movieDAO.updateMovieVideoKey(movieId, videoKey)
    }

    override suspend fun saveMovieDetail(movieDetail: MovieDetailDomainModel) {
        movieDAO.updateExistingMovieWithDetail(movieDetail.toMovieUpdated())
    }
}
