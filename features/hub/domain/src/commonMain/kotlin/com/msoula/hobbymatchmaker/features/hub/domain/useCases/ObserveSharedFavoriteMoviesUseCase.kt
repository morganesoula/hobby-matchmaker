package com.msoula.hobbymatchmaker.features.hub.domain.useCases

import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class ObserveSharedFavoriteMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(ids: List<Long>) = movieRepository.observeSharedFavoriteMovies(ids)
}
