package com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases

import com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories.MovieDetailRepository

class FetchMovieDetailUseCase(private val movieDetailRepository: MovieDetailRepository) {
    suspend operator fun invoke(
        movieId: Long,
        language: String
    ) = movieDetailRepository.fetchMovieDetail(movieId, language)
}
