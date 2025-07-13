package com.msoula.hobbymatchmaker.features.moviedetail.domain.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel

class FakeMovieDetailRemoteDataSource(
    private val fetchMovieDetailResult: Result<MovieDetailDomainModel?, MovieDetailDomainError> = Result.Success(
        MovieDetailDomainModel()
    ),
    private val fetchMovieDetailCreditResult: Result<MovieCastDomainModel?, MovieDetailDomainError> = Result.Success(
        MovieCastDomainModel()
    ),
    private val fetchMovieTrailerResult: Result<MovieVideoDomainModel?, MovieDetailDomainError> = Result.Success(
        MovieVideoDomainModel(
            key = "abc123",
            type = "",
            site = "YouTube"
        )
    )
) : MovieDetailRemoteDataSource {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailDomainModel?, MovieDetailDomainError> = fetchMovieDetailResult

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): Result<MovieCastDomainModel?, MovieDetailDomainError> = fetchMovieDetailCreditResult

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): Result<MovieVideoDomainModel?, MovieDetailDomainError> = fetchMovieTrailerResult
}
