package com.msoula.movies.domain

import com.msoula.database.data.local.MovieEntity
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getMoviesByPopularityDesc(): Flow<List<MovieEntity>>

    suspend fun refreshMovies()
}
