package com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.services

import com.msoula.hobbymatchmaker.feature.moviedetail.data.dataSources.remote.models.MovieVideosResponseRemoteModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val PARAMS_MOVIE_ID = "movie_id"
private const val PARAMS_LANGUAGE = "language"

interface MovieVideosService {
    @GET("movie/{$PARAMS_MOVIE_ID}/videos")
    suspend fun fetchMovieVideos(
        @Path(PARAMS_MOVIE_ID) movieId: Long,
        @Query(PARAMS_LANGUAGE) language: String
    ): Response<MovieVideosResponseRemoteModel>
}
