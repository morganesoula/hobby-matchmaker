package com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel

class MovieDetailRepository(
    private val movieDetailRemoteDataSource: MovieDetailRemoteDataSource
) {
    suspend fun fetchMovieDetail(movieId: Long): Result<MovieDetailDomainModel?> {
       return movieDetailRemoteDataSource.fetchMovieDetail(movieId)
    }
}
