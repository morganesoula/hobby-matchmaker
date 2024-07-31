package com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.local

import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import kotlinx.coroutines.flow.Flow

interface MovieDetailLocalDataSource {
    suspend fun updateMovieInfo(movie: MovieDetailDomainModel)
    suspend fun updateMovieWithActors(movie: MovieDetailDomainModel)
    fun getMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?>
}
