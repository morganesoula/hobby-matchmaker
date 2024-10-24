package com.msoula.hobbymatchmaker.feature.moviedetail.domain.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.errors.FetchMovieCreditRemoteError
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.errors.FetchMovieDetailRemoteError
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.errors.FetchMovieTrailerRemoteError
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieVideoDomainModel

interface MovieDetailRemoteDataSource {
    suspend fun fetchMovieDetail(movieId: Long, language: String):
        Result<MovieDetailDomainModel?, FetchMovieDetailRemoteError>

    suspend fun fetchMovieCredit(movieId: Long, language: String):
        Result<MovieCastDomainModel?, FetchMovieCreditRemoteError>

    suspend fun fetchMovieTrailer(movieId: Long, language: String):
        Result<MovieVideoDomainModel?, FetchMovieTrailerRemoteError>
}
