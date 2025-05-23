package com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel

interface MovieDetailRemoteDataSource {
    suspend fun fetchMovieDetail(movieId: Long, language: String):
        Result<MovieDetailDomainModel?, MovieDetailDomainError>

    suspend fun fetchMovieCredit(movieId: Long, language: String):
        Result<MovieCastDomainModel?, MovieDetailDomainError>

    suspend fun fetchMovieTrailer(movieId: Long, language: String):
        Result<MovieVideoDomainModel?, MovieDetailDomainError>
}
