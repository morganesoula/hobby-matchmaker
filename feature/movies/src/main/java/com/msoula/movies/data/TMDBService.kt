package com.msoula.movies.data

import com.msoula.movies.data.network.MovieNetwork
import retrofit2.http.GET
import retrofit2.http.Query

private const val PARAMS_SORT_BY = "sort_by"
private const val PARAMS_LANGUAGE = "language"

interface TMDBService {

    @GET("discover/movie")
    suspend fun getMoviesByPopularityDesc(
        @Query(PARAMS_SORT_BY) sorted: String,
        @Query(PARAMS_LANGUAGE) language: String
    ): MovieNetwork
}
