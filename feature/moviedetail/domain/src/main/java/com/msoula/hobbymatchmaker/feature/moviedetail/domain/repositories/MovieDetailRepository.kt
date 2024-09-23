package com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories

import com.msoula.hobbymatchmaker.core.common.ExternalServiceError
import com.msoula.hobbymatchmaker.core.common.Result
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
        return try {
            when (val infoResult =
                movieDetailRemoteDataSource.fetchMovieDetail(movieId, language)) {
                is Result.Success -> {
                    val actorsResult =
                        movieDetailRemoteDataSource.fetchMovieCredit(movieId, language)
                    when (actorsResult) {
                        is Result.Success -> {
                            val movieDetail =
                                MovieDetailDomainModel(
                                    id = infoResult.data!!.id,
                                    title = infoResult.data!!.title,
                                    genre = infoResult.data!!.genre,
                                    popularity = infoResult.data!!.popularity,
                                    releaseDate = infoResult.data!!.releaseDate,
                                    synopsis = infoResult.data!!.synopsis,
                                    status = infoResult.data!!.status,
                                    cast = actorsResult.data?.cast
                                )
                            movieDetailLocalDataSource.saveMovieDetail(movieDetail)
                            Result.Success(true)
                        }

                        is Result.Failure -> Result.Failure(actorsResult.error)
                    }
                }

                is Result.Failure -> Result.Failure(infoResult.error)
            }
        } catch (e: Exception) {
            Result.Failure(ExternalServiceError(e.message ?: "Unknown error"))
        }
    }

    suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> {
        return movieDetailLocalDataSource.observeMovieDetail(movieId)
    }

    suspend fun updateMovieVideoURI(movieId: Long, videoURI: String) {
        movieDetailLocalDataSource.updateMovieVideoUri(movieId, videoURI)
    }

    suspend fun fetchMovieTrailer(movieId: Long, language: String): Result<MovieVideoDomainModel?> {
        return movieDetailRemoteDataSource.fetchMovieTrailer(movieId, language)
    }
}
