package com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.local

import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieDetailLocalDataSource {
    suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?>
    suspend fun updateMovieVideoUri(
        movieId: Long,
        videoKey: String
    )

    suspend fun saveMovieDetail(movieDetail: MovieDetailDomainModel)
}
