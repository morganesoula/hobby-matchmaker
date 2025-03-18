package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local

import com.msoula.hobbymatchmaker.core.database.HMMLocalDatabase
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.mappers.toMovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.local.mappers.toMovieWithActors
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieDetailLocalDataSourceImpl(
    private val database: HMMLocalDatabase
) : MovieDetailLocalDataSource {

    override suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> {
        return database.movieDao().observeMovieWithActor(movieId).map {
            it.toMovieDetailDomainModel()
        }
    }

    override suspend fun updateMovieVideoUri(movieId: Long, videoKey: String) {
        database.movieDao().updateMovieVideoKey(movieId, videoKey)
    }

    override suspend fun saveMovieDetail(movieDetail: MovieDetailDomainModel) {
        database.movieDao().upsertMovieWithActors(movieDetail.toMovieWithActors())
    }
}
