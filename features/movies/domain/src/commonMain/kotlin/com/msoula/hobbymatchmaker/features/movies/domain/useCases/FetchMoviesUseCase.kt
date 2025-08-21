package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class FetchMoviesUseCase(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(language: String): R<Unit, AppError> = movieRepository.fetchMovies(language)
}
