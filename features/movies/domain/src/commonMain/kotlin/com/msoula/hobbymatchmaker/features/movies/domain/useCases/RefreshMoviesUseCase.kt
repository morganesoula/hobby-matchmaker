package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class RefreshMoviesUseCase(private val movieRepository: MovieRepository) {
    suspend operator fun invoke(language: String): AppResult<Unit, AppError> = movieRepository.refreshMovies(language)
}
