package com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.remote

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieInfoDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieVideoDomainModel

interface MovieDetailRemoteDataSource {
    suspend fun fetchMovieInfo(movieId: Long, language: String): Result<MovieInfoDomainModel?>
    suspend fun fetchMovieCredit(movieId: Long, language: String): Result<MovieCastDomainModel?>
    suspend fun fetchMovieTrailer(movieId: Long, language: String): Result<MovieVideoDomainModel?>
}
