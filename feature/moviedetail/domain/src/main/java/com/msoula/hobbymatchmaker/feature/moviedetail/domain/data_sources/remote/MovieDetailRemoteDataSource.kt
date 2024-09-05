package com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.remote

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieVideoDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieDetailRemoteDataSource {
    suspend fun fetchMovieDetail(movieId: Long, language: String): Result<MovieDetailDomainModel?>
    suspend fun fetchMovieCredit(movieId: Long, language: String): Result<MovieCastDomainModel?>
    suspend fun fetchMovieTrailer(movieId: Long, language: String): Result<MovieVideoDomainModel?>
    suspend fun saveMovieDetail(movieDetail: MovieDetailDomainModel)
    suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?>
    suspend fun updateMovieVideoUri(movieId: Long, videoPath: String)
}
