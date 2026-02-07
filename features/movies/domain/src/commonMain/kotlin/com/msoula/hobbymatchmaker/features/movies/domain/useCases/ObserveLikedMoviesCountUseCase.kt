package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class ObserveLikedMoviesCountUseCase(
    private val movieRepository: MovieRepository
) {
    operator fun invoke() = movieRepository.observeMoviesLikedCount()
}
