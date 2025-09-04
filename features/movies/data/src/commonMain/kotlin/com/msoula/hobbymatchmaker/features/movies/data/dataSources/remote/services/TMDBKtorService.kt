package com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.services

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.safeCall
import com.msoula.hobbymatchmaker.features.movies.data.dataSources.remote.models.MovieResponseRemoteModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.http.encodedPath

private const val PARAMS_LANGUAGE = "language"
private const val PARAMS_PAGE = "page"

interface TMDBKtorService {
    suspend fun getMoviesByPopularityDesc(
        language: String,
        page: Int
    ): AppResult<MovieResponseRemoteModel, AppError>
}

class TMDBKtorServiceImpl(private val client: HttpClient) : TMDBKtorService {
    override suspend fun getMoviesByPopularityDesc(
        language: String,
        page: Int
    ): AppResult<MovieResponseRemoteModel, AppError> = safeCall {
        client.request {
            method = HttpMethod.Get
            url { encodedPath = "movie/popular" }
            parameter(PARAMS_LANGUAGE, language)
            parameter(PARAMS_PAGE, page)
        }.body<MovieResponseRemoteModel>()
            .also {
                Logger.d("GetMovies: page=$page")
            }
    }
}
