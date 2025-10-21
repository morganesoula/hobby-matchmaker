package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class CheckMovieSynopsisValueUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Long): AppResult<Boolean, AppError> {
        return movieRepository.isSynopsisMovieAvailable(movieId)
    }
}
