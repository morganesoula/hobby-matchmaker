package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieResponseRemoteModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.http.HttpMethod

private const val PARAMS_LANGUAGE = "language"
private const val PARAMS_PAGE = "page"

interface TMDBKtorService {
    suspend fun getMoviesByPopularityDesc(
        language: String,
        page: Int
    ): Result<MovieResponseRemoteModel, TMDBKtorError>
}

class TMDBKtorServiceImpl(private val client: HttpClient) : TMDBKtorService {
    override suspend fun getMoviesByPopularityDesc(
        language: String,
        page: Int
    ): Result<MovieResponseRemoteModel, TMDBKtorError> {
        return try {
            val response = client.request {
                url("movie/popular")
                method = HttpMethod.Get
                parameter(PARAMS_LANGUAGE, language)
                parameter(PARAMS_PAGE, page)
            }.body<MovieResponseRemoteModel>()

            Result.Success(response)
        } catch (e: Exception) {
            Result.Failure(TMDBKtorError(e.message ?: "Error while fetching movies online"))
        }
    }

}

class TMDBKtorError(override val message: String) : AppError
