package com.msoula.hobbymatchmaker.feature.moviedetail.domain.use_cases

import com.msoula.hobbymatchmaker.feature.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.feature.moviedetail.domain.repositories.MovieDetailRepository
import kotlinx.coroutines.flow.Flow

class ObserveMovieDetailUseCase(private val movieDetailRepository: MovieDetailRepository) {
    suspend operator fun invoke(movieId: Long): Flow<MovieDetailDomainModel?> {
        return movieDetailRepository.observeMovieDetail(movieId)
    }
}
