package com.msoula.movies.domain.use_case

import com.msoula.movies.domain.MovieRepository

fun interface DeleteAllMovies : suspend () -> Unit

suspend fun deleteAllMovies(
    movieRepository: MovieRepository
) {
    movieRepository
        .deleteAllMovies()
}
