package com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieVideoDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories.MovieDetailRepository

class FetchMovieDetailTrailerUseCase(
    private val movieDetailRepository: MovieDetailRepository
) {
    suspend operator fun invoke(movieId: Long, language: String): Result<MovieVideoDomainModel?> =
        movieDetailRepository.fetchMovieTrailer(movieId, language)
}
