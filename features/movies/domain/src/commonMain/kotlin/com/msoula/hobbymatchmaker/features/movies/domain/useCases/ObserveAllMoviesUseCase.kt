package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.toStorageError
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

sealed class ObserveAllMoviesSuccess {
    data class Success(val movies: List<MovieDomainModel>) : ObserveAllMoviesSuccess()
    data object DataLoadedInDB : ObserveAllMoviesSuccess()
}

class ObserveAllMoviesUseCase(
    private val movieRepository: MovieRepository,
    private val fetchMoviesUseCase: FetchMoviesUseCase,
    private val dispatcher: CoroutineDispatcher
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(language: String): Flow<AppResult<ObserveAllMoviesSuccess, AppError>> =
        movieRepository.observeMovies()
            .distinctUntilChanged()
            .mapLatest { list ->
                if (list.isEmpty()) {
                    when (val fetch = fetchMoviesUseCase(language)) {
                        is AppResult.Success -> AppResult.Success(ObserveAllMoviesSuccess.DataLoadedInDB)
                        is AppResult.Failure -> AppResult.Failure(fetch.error)
                    }
                } else {
                    AppResult.Success(ObserveAllMoviesSuccess.Success(list))
                }
            }
            .catch { e -> emit(AppResult.Failure(e.toStorageError())) }
            .flowOn(dispatcher)
}
