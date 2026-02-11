package com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.toStorageError
import com.msoula.hobbymatchmaker.features.moviedetail.domain.models.MovieDetailDomainModel
import com.msoula.hobbymatchmaker.features.moviedetail.domain.repositories.MovieDetailRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
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
    private val syncMovieDetailUseCase: SyncMovieDetailUseCase,
    private val dispatcher: CoroutineDispatcher
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        movieId: Long,
        language: String
    ): Flow<AppResult<ObserveMovieSuccess, AppError>> =
        movieDetailRepository.observeMovieDetail(movieId)
            .distinctUntilChanged()
            .transformLatest { result ->
                when (result) {
                    is AppResult.Success -> {
                        val detail = result.data

                        if (detail.synopsis.isBlank() || !detail.hasCast) {
                            handleSync(movieId, language, detail)
                        } else {
                            emit(AppResult.Success(ObserveMovieSuccess.Success(detail)))
                        }
                    }

                    is AppResult.Failure -> emit(AppResult.Failure(result.error))
                }
            }
            .catch { e -> emit(AppResult.Failure(e.toStorageError())) }
            .flowOn(dispatcher)

    private suspend fun FlowCollector<AppResult<ObserveMovieSuccess, AppError>>.handleSync(
        movieId: Long,
        language: String,
        detail: MovieDetailDomainModel
    ) {
        when (val syncResult = syncMovieDetailUseCase(movieId, language, detail)) {
            is AppResult.Failure -> emit(AppResult.Failure(syncResult.error))
            is AppResult.Success -> emit(AppResult.Success(ObserveMovieSuccess.DataLoadedInDB))
        }
    }
}
