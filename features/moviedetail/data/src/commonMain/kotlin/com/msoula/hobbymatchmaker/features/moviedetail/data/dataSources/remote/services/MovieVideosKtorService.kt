package com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.services

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.safeCall
import com.msoula.hobbymatchmaker.features.moviedetail.data.dataSources.remote.models.MovieVideosResponseRemoteModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.http.encodedPath

interface MovieVideosKtorService {

    suspend fun fetchMovieVideos(
        movie: Long,
        language: String
    ): AppResult<MovieVideosResponseRemoteModel, AppError>
}

class MovieVideosKtorServiceImpl(private val client: HttpClient) : MovieVideosKtorService {

    override suspend fun fetchMovieVideos(
        movie: Long,
        language: String
    ): AppResult<MovieVideosResponseRemoteModel, AppError> = safeCall {
        client.request {
            method = HttpMethod.Get
            url { encodedPath = "movie/$movie/videos" }
            parameter("language", language)
        }.body<MovieVideosResponseRemoteModel>()
            .also {
                Logger.d("MovieVideos: id=$movie lang=$language")
            }
    }
}

