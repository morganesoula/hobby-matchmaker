package com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases

import com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories.MovieDetailRepository

class UpdateMovieVideoURIUseCase(
    private val movieDetailRepository: MovieDetailRepository
) {
    suspend operator fun invoke(
        movieId: Long,
        videoURI: String,
    ) {
        movieDetailRepository.updateMovieVideoURI(movieId, videoURI)
    }
}
