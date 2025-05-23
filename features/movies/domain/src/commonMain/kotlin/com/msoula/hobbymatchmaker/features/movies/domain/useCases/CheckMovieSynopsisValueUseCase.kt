package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class CheckMovieSynopsisValueUseCase(
    private val movieRepository: MovieRepository
) {

    suspend operator fun invoke(movieId: Long): Boolean {
        return movieRepository.isSynopsisMovieAvailable(movieId)
    }
}
