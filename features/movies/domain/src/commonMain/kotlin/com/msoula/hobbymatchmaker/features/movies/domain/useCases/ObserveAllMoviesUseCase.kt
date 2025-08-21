package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.core.common.toStorageError
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

sealed class ObserveAllMoviesSuccess {
    data class Success(val movies: List<MovieDomainModel>) : ObserveAllMoviesSuccess()
    data object DataLoadedInDB : ObserveAllMoviesSuccess()
}

class ObserveAllMoviesUseCase(
    private val movieRepository: MovieRepository,
    private val fetchMoviesUseCase: FetchMoviesUseCase,
    private val dispatcher: CoroutineDispatcher
) {

    operator fun invoke(language: String): Flow<R<ObserveAllMoviesSuccess, AppError>> =
        channelFlow {
            val job = launch {
                movieRepository.observeMovies()
                    .distinctUntilChanged()
                    .catch { e -> send(R.Failure(e.toStorageError())) }
                    .collect { list ->
                        if (list.isEmpty()) {
                            when (val fetch = fetchMoviesUseCase(language)) {
                                is R.Success -> send(R.Success(ObserveAllMoviesSuccess.DataLoadedInDB))
                                is R.Failure -> send(R.Failure(fetch.error))
                            }
                        } else {
                            send(R.Success(ObserveAllMoviesSuccess.Success(list)))
                        }
                    }
            }
            awaitClose { job.cancel() }
        }.flowOn(dispatcher)
}
