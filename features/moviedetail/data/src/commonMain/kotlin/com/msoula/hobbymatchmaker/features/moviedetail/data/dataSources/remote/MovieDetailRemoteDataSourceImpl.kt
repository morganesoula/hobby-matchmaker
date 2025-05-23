package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.network.safeKtorCall
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.mappers.toMovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.mappers.toMovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.mappers.toMovieVideoDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieDetailKtorService
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services.MovieVideosKtorService
import com.msoula.hobbymatchmaker.features.moviedetail.domain.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieVideoDomainModel
import kotlinx.io.IOException

class MovieDetailRemoteDataSourceImpl(
    private val movieDetailKtorService: MovieDetailKtorService,
    private val movieVideosKtorServiceImpl: MovieVideosKtorService
) : MovieDetailRemoteDataSource {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailDomainModel, MovieDetailDomainError> {
        return safeKtorCall(
            block = {
                val response = movieDetailKtorService.fetchMovieDetail(movieId, language)
                val data = when (response) {
                    is Result.Success -> response.data.toMovieDetailDomainModel()
                    else -> MovieDetailDomainModel()
                }
                Result.Success(data)
            },
            errorMapper = { throwable ->
                Logger.e("fetchMovieDetail error: ${throwable.message}")
                when (throwable) {
                    is IOException -> MovieDetailDomainError.NoConnection(throwable.message ?: "")
                    else -> MovieDetailDomainError.MovieDetailError(throwable.message ?: "")
                }
            }
        )
    }

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): Result<MovieCastDomainModel?, MovieDetailDomainError> {
        return safeKtorCall(
            block = {
                val response = movieDetailKtorService.fetchMovieCredits(movieId, language)
                val data = when (response) {
                    is Result.Success -> response.data.toMovieActorDomainModel()
                    else -> null
                }
                Result.Success(data)
            },
            errorMapper = { throwable ->
                Logger.e("FetchMovieCredit error: ${throwable.message}")
                when (throwable) {
                    is IOException -> MovieDetailDomainError.NoConnection(throwable.message ?: "")
                    else -> MovieDetailDomainError.CreditError(throwable.message ?: "")
                }
            }
        )
    }

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): Result<MovieVideoDomainModel?, MovieDetailDomainError> {
        return safeKtorCall(
            block = {
                val response = movieVideosKtorServiceImpl.fetchMovieVideos(movieId, language)
                val data = when (response) {
                    is Result.Success -> response.data.toMovieVideoDomainModel()
                    else -> null
                }

                Result.Success(data)
            },
            errorMapper = { throwable ->
                Logger.e("fetchMovieTrailer error: ${throwable.message}")

                when (throwable) {
                    is IOException -> MovieDetailDomainError.NoConnection(throwable.message ?: "")
                    else -> MovieDetailDomainError.TrailerError(throwable.message ?: "")
                }
            }
        )
    }
}


