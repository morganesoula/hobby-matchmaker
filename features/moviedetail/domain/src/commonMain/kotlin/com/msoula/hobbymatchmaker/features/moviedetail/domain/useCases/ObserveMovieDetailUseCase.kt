package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.moviedetail.domain.errors.MovieDetailDomainError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieActorDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class ObserveMovieDetailUseCase(
    private val movieDetailRepository: MovieDetailRepository,
    private val dispatcher: CoroutineDispatcher
) :
    FlowUseCase<Parameters.LongStringParam, ObserveMovieSuccess, ObserveMovieErrors>(
        dispatcher
    ) {
    override fun execute(parameters: Parameters.LongStringParam):
        Flow<Result<ObserveMovieSuccess, ObserveMovieErrors>> {

        return channelFlow {
            send(Result.Loading)
            movieDetailRepository.observeMovieDetail(parameters.longValue).collect { movieDetail ->

                when {
                    movieDetail == null -> send(Result.Failure(ObserveMovieErrors.Empty))

                    movieDetail.synopsis.isNullOrBlank() ->
                        send(fetchAndSaveMovieData(parameters.longValue, parameters.stringValue))

                    movieDetail.cast.isNullOrEmpty() -> {
                        when (val result = movieDetailRepository.fetchMovieCredit(
                            parameters.longValue,
                            parameters.stringValue
                        )) {
                            is Result.Success -> {
                                val cast = result.data?.takeIf { it.isNotEmpty() }
                                    ?: listOf(
                                        MovieActorDomainModel(
                                            name = "NO_CAST",
                                            role = "MARKER"
                                        )
                                    )

                                val updated = movieDetail.copy(cast = cast)
                                movieDetailRepository.saveMovieDetail(updated)
                                send(Result.Success(ObserveMovieSuccess.DataLoadedInDB))
                            }

                            is Result.Failure -> {
                                Logger.e("Error fetching cast: ${result.error.message}")
                                send(Result.Failure(mapCreditError(result.error as MovieDetailDomainError)))
                            }

                            else -> {
                                Logger.e("Unexpected error while fetching cast")
                                send(Result.Failure(ObserveMovieErrors.Error("Unexpected error")))
                            }
                        }
                    }

                    else -> send(Result.Success(ObserveMovieSuccess.Success(movieDetail)))
                }
            }
        }.flowOn(dispatcher)
    }

    private suspend fun fetchAndSaveMovieData(
        movieId: Long,
        language: String
    ): Result<ObserveMovieSuccess, ObserveMovieErrors> {
        val detailResult = movieDetailRepository.fetchMovieDetail(
            movieId = movieId,
            language = language
        )

        return when (detailResult) {
            is Result.Failure -> {
                Logger.e("FetchMovieDetail error: ${detailResult.error.message}")
                Result.Failure(mapDetailError(detailResult.error as MovieDetailDomainError))
            }

            is Result.Success -> {
                val creditResult = movieDetailRepository.fetchMovieCredit(
                    movieId = movieId,
                    language = language
                )

                val safeCast =
                    if (creditResult is Result.Success) creditResult.data else emptyList()

                val updatedMovie = detailResult.data.copy(cast = safeCast)
                movieDetailRepository.saveMovieDetail(updatedMovie)

                return Result.Success(ObserveMovieSuccess.DataLoadedInDB)
            }

            else -> {
                Logger.e("Unknown error")
                Result.Failure(ObserveMovieErrors.Error("Unknown error"))
            }
        }
    }
}

sealed class ObserveMovieSuccess {
    data class Success(val data: MovieDetailDomainModel) : ObserveMovieSuccess()
    data object DataLoadedInDB : ObserveMovieSuccess()
}

sealed class ObserveMovieErrors(override val message: String) : AppError {
    data object Empty : ObserveMovieErrors("")
    data object NoConnection : ObserveMovieErrors("")
    data object CreditError : ObserveMovieErrors("")
    data object MovieDetailError : ObserveMovieErrors("")
    data class Error(val error: String) : ObserveMovieErrors("")
}

private fun mapDetailError(error: MovieDetailDomainError): ObserveMovieErrors =
    when (error) {
        is MovieDetailDomainError.NoConnection -> ObserveMovieErrors.NoConnection
        is MovieDetailDomainError.MovieDetailError -> ObserveMovieErrors.MovieDetailError
        else -> ObserveMovieErrors.Error(error.message)
    }

private fun mapCreditError(error: MovieDetailDomainError): ObserveMovieErrors =
    when (error) {
        is MovieDetailDomainError.NoConnection -> ObserveMovieErrors.NoConnection
        is MovieDetailDomainError.CreditError -> ObserveMovieErrors.CreditError
        else -> ObserveMovieErrors.Error(error.message)
    }


