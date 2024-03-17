package com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.services

import com.msoula.hobbymatchmaker.features.movies.data.data_sources.remote.models.MovieResponseRemoteModel
import retrofit2.http.GET
import retrofit2.http.Query

private const val PARAMS_LANGUAGE = "language"
private const val PARAMS_PAGE = "page"

interface TMDBService {
    @GET("movie/popular")
    suspend fun getMoviesByPopularityDesc(
        @Query(PARAMS_LANGUAGE) language: String,
        @Query(PARAMS_PAGE) page: Int,
    ): MovieResponseRemoteModel
}
