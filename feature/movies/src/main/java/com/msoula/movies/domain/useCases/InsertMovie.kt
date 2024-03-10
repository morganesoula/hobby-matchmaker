package com.msoula.movies.domain.useCases

import com.msoula.movies.data.model.Movie
import com.msoula.movies.domain.MovieRepository

fun interface InsertMovieUseCase : suspend (Movie) -> Unit

suspend fun insertMovie(
    movieRepository: MovieRepository,
    movie: Movie,
) {
    movieRepository
        .insertMovie(movie)
}
