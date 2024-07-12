package com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.services

import com.msoula.hobbymatchmaker.feature.moviedetail.data.data_sources.remote.models.MovieDetailResponseRemoteModel
import retrofit2.Response
import retrofit2.http.GET

private const val PARAMS_MOVIE_ID = "movie_id"

interface MovieDetailService {
    @GET("movie/{$PARAMS_MOVIE_ID}")
    suspend fun getMovieDetail(movieId: Long): Response<MovieDetailResponseRemoteModel>
}
