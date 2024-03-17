package com.msoula.hobbymatchmaker.features.movies.domain.use_cases

import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class DeleteAllMoviesUseCase(private val movieRepository: MovieRepository) {
    suspend operator fun invoke() {
        movieRepository.deleteAllMovies()
    }
}
