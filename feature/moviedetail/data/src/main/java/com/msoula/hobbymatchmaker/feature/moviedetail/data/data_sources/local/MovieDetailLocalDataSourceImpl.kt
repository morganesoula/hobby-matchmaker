package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.local

import com.msoula.hobbymatchmaker.core.database.dao.MovieDAO
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.local.mappers.toMovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.local.mappers.toMovieWithActors
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieDetailLocalDataSourceImpl(
    private val movieDAO: MovieDAO
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
        movieDAO.upsertMovieWithActors(movieDetail.toMovieWithActors())
    }
}
