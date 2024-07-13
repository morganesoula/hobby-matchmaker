package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.network.execute
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers.toMovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.services.MovieDetailService
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel

class MovieDetailRemoteDataSourceImpl(
    private val movieDetailService: MovieDetailService
) : MovieDetailRemoteDataSource {

    override suspend fun fetchMovieDetail(movieId: Long): Result<MovieDetailDomainModel?> {
        return execute({
            movieDetailService.getMovieDetail(movieId)
        })
            .mapSuccess { response ->
                response.toMovieDetailDomainModel()
            }
    }
}
