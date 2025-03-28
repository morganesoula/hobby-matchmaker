package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.FlowUseCase
import com.msoula.hobbymatchmaker.core.common.Parameters
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class ObserveMovieDetailUseCase(
    private val movieDetailRepository: MovieDetailRepository,
    private val fetchMovieDetailUseCase: FetchMovieDetailUseCase,
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
                if (movieDetail != null && movieDetail.synopsis.isNullOrBlank()) {
                    when (val fetchStatus = fetchMovieDetailUseCase(
                        movieId = parameters.longValue,
                        language = parameters.stringValue
                    )) {
                        is Result.Success -> send(Result.Success(ObserveMovieSuccess.DataLoadedInDB))
                        is Result.Failure -> send(Result.Failure(fetchStatus.error))
                        is Result.Loading -> send(Result.Loading)
                    }
                } else if (movieDetail != null) {
                    send(Result.Success(ObserveMovieSuccess.Success(movieDetail)))
                } else send(Result.Failure(ObserveMovieErrors.Empty))
            }
        }.flowOn(dispatcher)
    }
}

sealed class ObserveMovieSuccess {
    data class Success(val data: MovieDetailDomainModel) : ObserveMovieSuccess()
    data object DataLoadedInDB : ObserveMovieSuccess()
}

sealed class ObserveMovieErrors(override val message: String) : AppError {
    data object Empty : ObserveMovieErrors("")
    data class Error(val error: String) : ObserveMovieErrors("")
}


