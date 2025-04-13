package com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.ExternalServiceError
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.safeCall
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.local.MovieDetailLocalDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.UpdateMovieTrailerLocalError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import kotlinx.coroutines.flow.Flow

class MovieDetailRepositoryImpl(
    private val movieDetailRemoteDataSource: MovieDetailRemoteDataSource,
    private val movieDetailLocalDataSource: MovieDetailLocalDataSource
) : MovieDetailRepository {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailDomainModel, MovieDetailDomainError> {
        return try {
            when (val result =
                movieDetailRemoteDataSource.fetchMovieDetail(movieId, language)) {

                is Result.Success -> {
                    result.data?.let { data ->
                        Result.Success(
                            MovieDetailDomainModel(
                                id = data.id,
                                title = data.title,
                                genre = data.genre,
                                popularity = data.popularity,
                                releaseDate = data.releaseDate,
                                synopsis = data.synopsis,
                                status = data.status,
                            )
                        )
                    } ?: Result.Failure(EmptyDataError("Empty data after successful fetch"))
                }

                is Result.Failure -> Result.Failure(result.error)
                Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Failure(ExternalServiceError(e.message ?: "Unknown error"))
        }
    }

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): Result<List<MovieActorDomainModel>?, MovieDetailDomainError> {
        return try {
            val result =
                movieDetailRemoteDataSource.fetchMovieCredit(movieId, language)

            when (result) {
                is Result.Success -> Result.Success(result.data?.cast)
                is Result.Failure -> Result.Failure(result.error)
                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Failure(ExternalServiceError(e.message ?: "Unknown error"))
        }
    }

    override suspend fun saveMovieDetail(movieDetailDomainModel: MovieDetailDomainModel) {
        movieDetailLocalDataSource.saveMovieDetail(movieDetailDomainModel)
    }

    override suspend fun observeMovieDetail(movieId: Long): Flow<MovieDetailDomainModel?> {
        return movieDetailLocalDataSource.observeMovieDetail(movieId)
    }

    override suspend fun updateMovieVideoURI(
        movieId: Long,
        videoURI: String
    ): Result<Boolean, UpdateMovieTrailerLocalError> {
        return safeCall(appError = { errorMessage ->
            UpdateMovieTrailerLocalError(
                "Error while updating movie trailer in DB + $errorMessage"
            )
        }) {
            movieDetailLocalDataSource.updateMovieVideoUri(movieId, videoURI)
            true
        }
    }

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): Result<MovieVideoDomainModel?, MovieDetailDomainError> {
        return movieDetailRemoteDataSource.fetchMovieTrailer(movieId, language)
    }
}

data class EmptyDataError(override val message: String) : AppError
