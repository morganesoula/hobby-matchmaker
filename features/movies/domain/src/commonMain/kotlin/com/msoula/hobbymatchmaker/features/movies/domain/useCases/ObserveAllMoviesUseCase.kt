package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.toStorageError
import com.msoula.hobbymatchmaker.features.movies.domain.models.MovieDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

sealed class ObserveAllMoviesSuccess {
    data class Success(val movies: List<MovieDomainModel>) : ObserveAllMoviesSuccess()
}

class ObserveAllMoviesUseCase(
    private val movieRepository: MovieRepository,
    private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(): Flow<AppResult<ObserveAllMoviesSuccess, AppError>> {
        return movieRepository.observeMovies()
            .distinctUntilChanged()
            .map<List<MovieDomainModel>, AppResult<ObserveAllMoviesSuccess, AppError>> { list ->
                AppResult.Success(ObserveAllMoviesSuccess.Success(list))
            }
            .catch { e ->
                Logger.e("ObserveAllMoviesUseCase - Error: $e")
                val error: AppResult<ObserveAllMoviesSuccess, AppError> = AppResult.Failure(e.toStorageError())
                emit(error)
            }
            .flowOn(dispatcher)
    }
}
