package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.core.common.toStorageError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

sealed class ObserveMovieSuccess {
    data class Success(val data: MovieDetailDomainModel) : ObserveMovieSuccess()
    data object DataLoadedInDB : ObserveMovieSuccess()
}

class ObserveMovieDetailUseCase(
    private val movieDetailRepository: MovieDetailRepository,
    private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(movieId: Long, language: String): Flow<R<ObserveMovieSuccess, AppError>> =
        channelFlow {
            val job = launch {
                movieDetailRepository.observeMovieDetail(movieId)
                    .catch { e -> send(R.Failure(e.toStorageError())) }
                    .collect { detail ->
                        Logger.d("DetailUseCase: observed title: ${detail?.title}")

                        when {
                            detail == null -> send(R.Failure(AppError.Domain.NotFound))

                            detail.synopsis.isNullOrBlank() -> {
                                when (val detailResult =
                                    movieDetailRepository.fetchMovieDetail(movieId, language)) {
                                    is R.Failure -> send(R.Failure(detailResult.error))
                                    is R.Success -> {
                                        val creditResult = movieDetailRepository.fetchMovieCredit(
                                            movieId,
                                            language
                                        )
                                        val safeCast: List<MovieActorDomainModel> =
                                            (creditResult as? R.Success)?.data?.takeIf { it.isNotEmpty() }
                                                ?: emptyList()

                                        val updated = detailResult.data?.copy(cast = safeCast)
                                        updated?.let {
                                            movieDetailRepository.saveMovieDetail(it)
                                                .let { result ->
                                                    if (result is R.Failure) send(R.Failure(result.error))
                                                }
                                        }

                                        send(R.Success(ObserveMovieSuccess.DataLoadedInDB))
                                    }
                                }
                            }

                            detail.cast.isNullOrEmpty() -> {
                                when (val creditResult =
                                    movieDetailRepository.fetchMovieCredit(movieId, language)) {
                                    is R.Failure -> send(R.Failure(creditResult.error))
                                    is R.Success -> {
                                        val cast =
                                            creditResult.data?.takeIf { it.isNotEmpty() } ?: listOf(
                                                MovieActorDomainModel(
                                                    name = "NO_CAST",
                                                    role = "MARKER"
                                                )
                                            )

                                        val updated = detail.copy(cast = cast)
                                        movieDetailRepository.saveMovieDetail(updated)
                                        send(R.Success(ObserveMovieSuccess.DataLoadedInDB))
                                    }
                                }
                            }

                            else -> send(R.Success(ObserveMovieSuccess.Success(detail)))
                        }
                    }
            }

            awaitClose { job.cancel() }
        }.flowOn(dispatcher)
}
