package com.msoula.movies.domain.useCases

import com.msoula.movies.domain.MovieRepository

fun interface DeleteAllMovies : suspend () -> Unit

suspend fun deleteAllMovies(movieRepository: MovieRepository) {
    movieRepository
        .deleteAllMovies()
}
