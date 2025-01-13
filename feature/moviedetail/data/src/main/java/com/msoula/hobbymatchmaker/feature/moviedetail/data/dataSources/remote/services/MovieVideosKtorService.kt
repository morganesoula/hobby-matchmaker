package com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.services

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.models.MovieVideosResponseRemoteModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.request
import io.ktor.http.HttpMethod
import io.ktor.http.encodedPath

interface MovieVideosKtorService {

    suspend fun fetchMovieVideos(
        movie: Long,
        language: String
    ): Result<MovieVideosResponseRemoteModel, FetchMovieVideosKtorError>
}

class MovieVideosKtorServiceImpl(private val client: HttpClient) : MovieVideosKtorService {

    override suspend fun fetchMovieVideos(
        movie: Long,
        language: String
    ): Result<MovieVideosResponseRemoteModel, FetchMovieVideosKtorError> {
        return try {
            val response = client.request {
                url {
                    encodedPath = "movie/$movie/videos"
                    parameters.append("language", language)
                }
                method = HttpMethod.Get
            }.body<MovieVideosResponseRemoteModel>()

            Result.Success(response)
        } catch (e: Exception) {
            Result.Failure(FetchMovieVideosKtorError(e.message.toString()))
        }
    }
}

class FetchMovieVideosKtorError(override val message: String) : AppError
