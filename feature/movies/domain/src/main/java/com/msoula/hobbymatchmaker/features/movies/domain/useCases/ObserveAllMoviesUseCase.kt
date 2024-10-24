package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class ObserveAllMoviesUseCase(
    private val movieRepository: MovieRepository,
    private val fetchMoviesUseCase: FetchMoviesUseCase,
    dispatcher: CoroutineDispatcher
) : FlowUseCase<Parameters.StringParam, ObserveAllMoviesSuccess, ObserveAllMoviesErrors>(dispatcher) {

    override fun execute(parameters: Parameters.StringParam):
        Flow<Result<ObserveAllMoviesSuccess, ObserveAllMoviesErrors>> {
        return channelFlow {
            send(Result.Loading)

            movieRepository.observeMovies().collect { list ->
                if (list.isEmpty()) {
                    when (val fetchStatus = fetchMoviesUseCase(parameters.value)) {
                        is Result.Success -> send(Result.Success(ObserveAllMoviesSuccess.DataLoadedInDB))
                        is Result.Failure -> send(Result.Failure(fetchStatus.error))
                        is Result.Loading -> send(Result.Success(ObserveAllMoviesSuccess.Loading))
                        is Result.BusinessRuleError -> send(
                            Result.BusinessRuleError(
                                ObserveAllMoviesErrors.Error(fetchStatus.error)
                            )
                        )
                    }
                } else {
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

sealed class ObserveAllMoviesErrors {
    data object Empty : ObserveAllMoviesErrors()
    data class Error(val error: AppError) : ObserveAllMoviesErrors()
}
