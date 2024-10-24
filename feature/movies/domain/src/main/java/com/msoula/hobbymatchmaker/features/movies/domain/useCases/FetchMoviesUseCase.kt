package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.errors.MovieErrors
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class FetchMoviesUseCase(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(language: String): Result<Unit, MovieErrors> = movieRepository.fetchMovies(language)
}
