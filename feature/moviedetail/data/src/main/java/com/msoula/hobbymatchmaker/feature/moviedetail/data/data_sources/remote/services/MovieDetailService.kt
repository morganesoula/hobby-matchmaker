package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.services

import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.models.CastResponseRemoteModel
import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.models.MovieInfoResponseRemoteModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val PARAMS_MOVIE_ID = "movie_id"
private const val PARAMS_LANGUAGE = "language"

interface MovieDetailService {
    @GET("movie/{$PARAMS_MOVIE_ID}")
    suspend fun fetchMovieDetail(
        @Path(PARAMS_MOVIE_ID) movieId: Long,
        @Query(PARAMS_LANGUAGE) language: String
    ): Response<MovieInfoResponseRemoteModel>

    @GET("movie/{$PARAMS_MOVIE_ID}/credits")
    suspend fun fetchMovieCredits(
        @Path(PARAMS_MOVIE_ID) movieId: Long,
        @Query(PARAMS_LANGUAGE) language: String
    ): Response<CastResponseRemoteModel>
}
