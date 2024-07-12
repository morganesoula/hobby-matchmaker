package com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases

import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories.MovieDetailRepository

class FetchMovieDetailUseCase(private val movieDetailRepository: MovieDetailRepository) {
    suspend operator fun invoke(movieId: Long): Result<MovieDetailDomainModel?> = movieDetailRepository.fetchMovieDetail(movieId)
}
