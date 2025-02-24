package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository

class UpdateMovieVideoURIUseCase(
    private val movieDetailRepository: MovieDetailRepository
) {
    suspend operator fun invoke(
        movieId: Long,
        videoURI: String
    ) = movieDetailRepository.updateMovieVideoURI(movieId, videoURI)
}
