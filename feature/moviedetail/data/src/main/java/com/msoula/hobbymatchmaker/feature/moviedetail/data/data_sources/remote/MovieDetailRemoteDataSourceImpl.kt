package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.network.execute
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers.toMovieActorDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.mappers.toMovieInfoDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.services.MovieDetailService
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieInfoDomainModel

class MovieDetailRemoteDataSourceImpl(
    private val movieDetailService: MovieDetailService
) : MovieDetailRemoteDataSource {

    override suspend fun fetchMovieInfo(movieId: Long, language: String): Result<MovieInfoDomainModel?> {
        return execute({
            movieDetailService.fetchMovieDetail(movieId, language)
        })
            .mapSuccess { response ->
                response.toMovieInfoDomainModel()
            }
    }

    override suspend fun fetchMovieCredit(movieId: Long, language: String): Result<MovieCastDomainModel?> {
        return execute({
            movieDetailService.fetchMovieCredits(movieId, language)
        })
            .mapSuccess { response ->
                response.toMovieActorDomainModel()
            }
    }
}
