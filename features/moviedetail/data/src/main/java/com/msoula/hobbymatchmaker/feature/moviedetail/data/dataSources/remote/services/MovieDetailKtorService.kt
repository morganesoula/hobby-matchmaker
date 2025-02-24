package com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.services

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.models.CastResponseRemoteModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.models.MovieDetailResponseRemoteModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.http.encodedPath

private const val PARAMS_MOVIE_ID = "movie_id"
private const val PARAMS_LANGUAGE = "language"

interface MovieDetailKtorService {
    suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailResponseRemoteModel, MovieDetailKtorError>

    suspend fun fetchMovieCredits(
        movieId: Long,
        language: String
    ): Result<CastResponseRemoteModel, MovieCreditsKtorError>
}

class MovieDetailKtorServiceImpl(private val client: HttpClient) : MovieDetailKtorService {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): Result<MovieDetailResponseRemoteModel, MovieDetailKtorError> {
        return try {
            val response = client.request {
                url {
                    encodedPath = "movie/$movieId"
                    parameters.append(PARAMS_LANGUAGE, language)
                }
                method = HttpMethod.Get
            }.body<MovieDetailResponseRemoteModel>()

            Result.Success(response)
        } catch (e: Exception) {
            Result.Failure(
                MovieDetailKtorError(
                    e.message ?: "Error while fetching movie detail online"
                )
            )
        }
    }

    override suspend fun fetchMovieCredits(
        movieId: Long,
        language: String
    ): Result<CastResponseRemoteModel, MovieCreditsKtorError> {
        return try {
            val response = client.request {
                url {
                    encodedPath = "movie/$movieId/credits"
                    parameters.append(PARAMS_LANGUAGE, language)
                }
                method = HttpMethod.Get
            }.body<CastResponseRemoteModel>()

            Result.Success(response)
        } catch (e: Exception) {
            Result.Failure(MovieCreditsKtorError(e.message.toString()))
        }
    }
}

class MovieDetailKtorError(override val message: String) : AppError
class MovieCreditsKtorError(override val message: String) : AppError
