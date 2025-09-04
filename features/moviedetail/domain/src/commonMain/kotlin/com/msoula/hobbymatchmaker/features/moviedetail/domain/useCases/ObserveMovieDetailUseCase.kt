package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.toStorageError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transformLatest

sealed class ObserveMovieSuccess {
    data class Success(val data: MovieDetailDomainModel) : ObserveMovieSuccess()
    data object DataLoadedInDB : ObserveMovieSuccess()
}

class ObserveMovieDetailUseCase(
    private val movieDetailRepository: MovieDetailRepository,
    private val dispatcher: CoroutineDispatcher
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(movieId: Long, language: String): Flow<AppResult<ObserveMovieSuccess, AppError>> =
        movieDetailRepository.observeMovieDetail(movieId)
            .distinctUntilChanged()
            .transformLatest { detail ->
                when {
                    detail == null -> emit(AppResult.Failure(AppError.Domain.NotFound))

                    detail.synopsis.isNullOrBlank() -> {
                        when (val detailResult =
                            movieDetailRepository.fetchMovieDetail(movieId, language)) {
                            is AppResult.Failure -> emit(AppResult.Failure(detailResult.error))
                            is AppResult.Success -> {
                                val creditResult = movieDetailRepository.fetchMovieCredit(
                                    movieId,
                                    language
                                )
                                val safeCast: List<MovieActorDomainModel> = when (creditResult) {
                                    is AppResult.Success -> creditResult.data?.takeIf { it.isNotEmpty() }
                                        ?: emptyList()

                                    is AppResult.Failure -> emptyList()
                                }

                                detailResult.data?.copy(cast = safeCast)?.let { updated ->
                                    when (val result =
                                        movieDetailRepository.saveMovieDetail(updated)) {
                                        is AppResult.Failure -> emit(AppResult.Failure(result.error))
                                        is AppResult.Success -> emit(AppResult.Success(ObserveMovieSuccess.DataLoadedInDB))
                                    }
                                } ?: emit(AppResult.Failure(AppError.Domain.NotFound))
                            }
                        }
                    }

                    detail.cast.isNullOrEmpty() -> {
                        when (val creditResult =
                            movieDetailRepository.fetchMovieCredit(movieId, language)) {
                            is AppResult.Failure -> emit(AppResult.Failure(creditResult.error))
                            is AppResult.Success -> {
                                val cast =
                                    creditResult.data?.takeIf { it.isNotEmpty() } ?: listOf(
                                        MovieActorDomainModel(
                                            name = "NO_CAST",
                                            role = "MARKER"
                                        )
                                    )

                                when (val result =
                                    movieDetailRepository.saveMovieDetail(detail.copy(cast = cast))) {
                                    is AppResult.Failure -> emit(AppResult.Failure(result.error))
                                    is AppResult.Success -> emit(AppResult.Success(ObserveMovieSuccess.DataLoadedInDB))
                                }
                            }
                        }
                    }

                    else -> emit(AppResult.Success(ObserveMovieSuccess.Success(detail)))
                }
            }
            .catch { e -> emit(AppResult.Failure(e.toStorageError())) }
            .flowOn(dispatcher)
}
