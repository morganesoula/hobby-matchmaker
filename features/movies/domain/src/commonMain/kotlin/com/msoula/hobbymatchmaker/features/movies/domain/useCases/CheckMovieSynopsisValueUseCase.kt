package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class CheckMovieSynopsisValueUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Long): R<Boolean, AppError> {
        return movieRepository.isSynopsisMovieAvailable(movieId)
    }
}
