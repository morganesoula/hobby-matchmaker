package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository

class UpdateMovieVideoURIUseCase(
    private val movieDetailRepository: MovieDetailRepository
) {
    suspend operator fun invoke(
        movieId: Long,
        videoURI: String
    ): AppResult<Unit, AppError> = movieDetailRepository.updateMovieVideoURI(movieId, videoURI)
}
