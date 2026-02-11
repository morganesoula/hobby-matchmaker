package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository

class SyncMovieDetailUseCase(
    private val movieDetailRepository: MovieDetailRepository
) {
    suspend operator fun invoke(
        movieId: Long,
        language: String,
        currentDetail: MovieDetailDomainModel
    ): AppResult<Unit, AppError> =
        if (currentDetail.synopsis.isBlank()) {
            syncDetailAndCredits(movieId, language)
        } else {
            syncCreditsOnly(movieId, language, currentDetail)
        }

    private suspend fun syncDetailAndCredits(
        movieId: Long,
        language: String
    ): AppResult<Unit, AppError> =
        when (val detailResult = movieDetailRepository.fetchMovieDetail(movieId, language)) {
            is AppResult.Failure -> AppResult.Failure(detailResult.error)
            is AppResult.Success -> {
                val credits = fetchCreditsOrEmpty(movieId, language)
                val updated = detailResult.data.copy(cast = credits, hasCast = true)
                movieDetailRepository.saveMovieDetail(updated)
            }
        }

    private suspend fun syncCreditsOnly(
        movieId: Long,
        language: String,
        currentDetail: MovieDetailDomainModel
    ): AppResult<Unit, AppError> =
        when (val creditResult = movieDetailRepository.fetchMovieCredit(movieId, language)) {
            is AppResult.Failure -> AppResult.Failure(creditResult.error)
            is AppResult.Success -> {
                val updated = currentDetail.copy(cast = creditResult.data, hasCast = true)
                movieDetailRepository.saveMovieDetail(updated)
            }
        }

    private suspend fun fetchCreditsOrEmpty(
        movieId: Long,
        language: String
    ): List<MovieActorDomainModel> =
        when (val result = movieDetailRepository.fetchMovieCredit(movieId, language)) {
            is AppResult.Success -> result.data
            is AppResult.Failure -> emptyList()
        }
}
