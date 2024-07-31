package com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.remote

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieInfoDomainModel

interface MovieDetailRemoteDataSource {
    suspend fun fetchMovieInfo(movieId: Long, language: String): Result<MovieInfoDomainModel?>
    suspend fun fetchMovieCredit(movieId: Long, language: String): Result<MovieCastDomainModel?>
}
