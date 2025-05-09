package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.errors.MovieErrors
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class ObserveAllMoviesUseCase(
    private val movieRepository: MovieRepository,
    private val fetchMoviesUseCase: FetchMoviesUseCase,
    dispatcher: CoroutineDispatcher
) : FlowUseCase<Parameters.StringParam, ObserveAllMoviesSuccess, ObserveAllMoviesErrors>(dispatcher) {

    override fun execute(parameters: Parameters.StringParam):
        Flow<Result<ObserveAllMoviesSuccess, ObserveAllMoviesErrors>> {
        return channelFlow {
            movieRepository.observeMovies().distinctUntilChanged().collect { list ->
                if (list.isEmpty()) {
                    Logger.d("List is empty in DB so fetching data")
                    send(Result.Success(ObserveAllMoviesSuccess.Loading))

                    when (val fetchStatus = fetchMoviesUseCase(parameters.value)) {
                        is Result.Success -> send(Result.Success(ObserveAllMoviesSuccess.DataLoadedInDB))
                        is Result.Failure -> {
                            val error = when (fetchStatus.error) {
                                is MovieErrors.NetworkError -> ObserveAllMoviesErrors.NetworkError(
                                    fetchStatus.error.message
                                )

                                is MovieErrors.ApiError -> ObserveAllMoviesErrors.ApiError(
                                    fetchStatus.error.message
                                )

                                else -> ObserveAllMoviesErrors.UnknownError(fetchStatus.error.message)
                            }
                            send(Result.Failure(error))
                        }

                        else -> send(Result.Success(ObserveAllMoviesSuccess.Loading))
                    }
                } else {
                    Logger.d("List is not empty: ${list.first().title} - ${list.first().localCoverFilePath}")
                    send(Result.Success(ObserveAllMoviesSuccess.Success(list)))
                }
            }
        }
    }
}

sealed class ObserveAllMoviesSuccess {
    data class Success(val movies: List<MovieDomainModel>) : ObserveAllMoviesSuccess()
    data object Loading : ObserveAllMoviesSuccess()
    data object DataLoadedInDB : ObserveAllMoviesSuccess()
}

sealed class ObserveAllMoviesErrors(override val message: String) : AppError {
    data object Empty : ObserveAllMoviesErrors("")
    data class NetworkError(val networkErrorMessage: String) :
        ObserveAllMoviesErrors(networkErrorMessage)

    data class ApiError(val apiErrorMessage: String) : ObserveAllMoviesErrors(apiErrorMessage)
    data class UnknownError(val unknownErrorMessage: String) :
        ObserveAllMoviesErrors(unknownErrorMessage)
}
