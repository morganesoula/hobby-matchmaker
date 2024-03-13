package com.msoula.movies.domain.useCases

import com.msoula.movies.domain.MovieRepository

fun interface SetMovieFavoriteUseCase : suspend (Long, Boolean) -> Unit

suspend fun setMovieFavorite(
    movieRepository: MovieRepository,
    id: Long,
    isFavorite: Boolean,
) {
    movieRepository.updateMovie(id, isFavorite)
}
