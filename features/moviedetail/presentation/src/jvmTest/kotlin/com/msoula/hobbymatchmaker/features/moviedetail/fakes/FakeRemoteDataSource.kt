package com.msoula.hobbymatchmaker.features.moviedetail.fakes

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainErrorHMM
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel

class FakeRemoteDataSource(
    private val fetchMovieDetailResult: Result<MovieDetailDomainModel?, MovieDetailDomainErrorHMM> =
        Result.Success(MovieDetailDomainModel()),
    private val fetchMovieCreditResult: Result<MovieCastDomainModel?, MovieDetailDomainErrorHMM> =
        Result.Success(MovieCastDomainModel()),
    private val fetchMovieTrailerResult: Result<MovieVideoDomainModel?, MovieDetailDomainErrorHMM> =
        Result.Success(
            MovieVideoDomainModel(
                key = "/testKey",
                type = "video",
                site = "YouTube"
            )
        )
) : MovieDetailRemoteDataSource {
    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailDomainModel?, MovieDetailDomainErrorHMM> = fetchMovieDetailResult

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): Result<MovieCastDomainModel?, MovieDetailDomainErrorHMM> = fetchMovieCreditResult

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): Result<MovieVideoDomainModel?, MovieDetailDomainErrorHMM> = fetchMovieTrailerResult
}
