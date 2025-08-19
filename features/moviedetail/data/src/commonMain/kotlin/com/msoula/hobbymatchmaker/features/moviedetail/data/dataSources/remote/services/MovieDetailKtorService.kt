package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.core.common.safeCall
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.CastResponseRemoteModel
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieDetailResponseRemoteModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.http.encodedPath

private const val PARAMS_LANGUAGE = "language"

interface MovieDetailKtorService {
    suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): R<MovieDetailResponseRemoteModel, AppError>

    suspend fun fetchMovieCredits(
        movieId: Long,
        language: String
    ): R<CastResponseRemoteModel, AppError>
}

class MovieDetailKtorServiceImpl(private val client: HttpClient) : MovieDetailKtorService {

    override suspend fun fetchMovieDetail(
        movieId: Long,
        language: String
    ): R<MovieDetailResponseRemoteModel, AppError> = safeCall {
        client.request {
            method = HttpMethod.Get
            url { encodedPath = "movie/$movieId" }
            parameter(PARAMS_LANGUAGE, language)
        }.body<MovieDetailResponseRemoteModel>()
            .also {
                Logger.d("MovieDetail: id=$movieId lang=$language")
            }
    }

    override suspend fun fetchMovieCredits(
        movieId: Long,
        language: String
    ): R<CastResponseRemoteModel, AppError> = safeCall {
        client.request {
            method = HttpMethod.Get
            url { encodedPath = "movie/$movieId/credits" }
            parameter(PARAMS_LANGUAGE, language)
        }.body<CastResponseRemoteModel>()
            .also {
                Logger.d("MovieCredits: id=$movieId lang=$language")
            }
    }
}
