package com.msoula.hobbymatchmaker.features.movies.domain.use_cases

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class FetchMoviesUseCase(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(language: String): Result<Unit> = movieRepository.fetchMovies(language)
}
