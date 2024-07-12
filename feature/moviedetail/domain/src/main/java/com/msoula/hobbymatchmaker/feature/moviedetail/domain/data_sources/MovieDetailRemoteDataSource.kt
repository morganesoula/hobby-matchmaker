package com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel

interface MovieDetailRemoteDataSource {
    suspend fun fetchMovieDetail(movieId: Long): Result<MovieDetailDomainModel?>
}
