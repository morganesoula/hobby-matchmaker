package com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote

import android.util.Log
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.mappers.toMovieActorDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.mappers.toMovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.mappers.toMovieVideoDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.services.MovieDetailKtorService
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.services.MovieVideosKtorService
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.dataSources.remote.MovieDetailRemoteDataSource
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.errors.FetchMovieCreditRemoteError
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.errors.FetchMovieDetailRemoteError
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.errors.FetchMovieTrailerRemoteError
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieCastDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieVideoDomainModel

class MovieDetailRemoteDataSourceImpl(
    private val movieDetailKtorService: MovieDetailKtorService,
    private val movieVideosKtorServiceImpl: MovieVideosKtorService
) : MovieDetailRemoteDataSource {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailDomainModel, FetchMovieDetailRemoteError> {
        return try {
            val response = movieDetailKtorService.fetchMovieDetail(movieId, language)
            val data = when (response) {
                is Result.Success -> response.data.toMovieDetailDomainModel()
                else -> MovieDetailDomainModel()
            }

            Result.Success(data)
        } catch (e: Exception) {
            Log.d("HMM", "fetchMovieDetail error: ${e.message}")
            Result.Failure(FetchMovieDetailRemoteError(e.message.toString()))
        }
    }

    override suspend fun fetchMovieCredit(
        movieId: Long,
        language: String
    ): Result<MovieCastDomainModel?, FetchMovieCreditRemoteError> {
        return try {
            val response = movieDetailKtorService.fetchMovieCredits(movieId, language)
            val data = when (response) {
                is Result.Success -> response.data.toMovieActorDomainModel()
                else -> null
            }
            Result.Success(data)
        } catch (e: Exception) {
            Log.d("HMM", "fetchMovieCredit error: ${e.message}")
            Result.Failure(FetchMovieCreditRemoteError(e.message.toString()))
        }
    }

    override suspend fun fetchMovieTrailer(
        movieId: Long,
        language: String
    ): Result<MovieVideoDomainModel?, FetchMovieTrailerRemoteError> {
        return try {
            val response = movieVideosKtorServiceImpl.fetchMovieVideos(movieId, language)
            val data = when (response) {
                is Result.Success -> response.data.toMovieVideoDomainModel()
                else -> null
            }

            Result.Success(data)
        } catch (e: Exception) {
            Log.d("HMM", "fetchMovieTrailer error: ${e.message}")
            Result.Failure(FetchMovieTrailerRemoteError(e.message.toString()))
        }
    }
}
