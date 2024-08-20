package com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.data_sources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieVideoDomainModel
import kotlinx.coroutines.flow.Flow

class MovieDetailRepository(
    private val movieDetailRemoteDataSource: MovieDetailRemoteDataSource,
    private val movieDetailLocalDataSource: MovieDetailLocalDataSource
) {
    suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<Boolean> {
        movieDetailRemoteDataSource.fetchMovieInfo(movieId, language)
            .mapSuccess { info ->
                movieDetailRemoteDataSource.fetchMovieCredit(movieId, language)
                    .mapSuccess { actors ->
                        info?.let {
                            val movieDetail = MovieDetailDomainModel(it, actors)
                            movieDetailLocalDataSource.updateMovieWithActors(movieDetail)
                        }
                    }
            }
            .mapError {
                return@mapError it
            }

        return Result.Success(true)
    }

    fun getMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> {
        return movieDetailLocalDataSource.getMovieDetail(movieId)
    }

    suspend fun updateMovieVideoURI(movieId: Long, videoURI: String) {
        movieDetailLocalDataSource.updateMovieVideoURI(movieId, videoURI)
    }

    suspend fun fetchMovieTrailer(movieId: Long, language: String): Result<MovieVideoDomainModel?> {
        return movieDetailRemoteDataSource.fetchMovieTrailer(movieId, language)
    }
}
