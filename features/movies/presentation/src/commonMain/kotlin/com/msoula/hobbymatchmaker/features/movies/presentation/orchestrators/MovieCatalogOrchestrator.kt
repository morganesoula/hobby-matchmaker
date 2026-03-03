package com.msoula.hobbymatchmaker.features.movies.presentation.orchestrators

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.domain.models.PaginationInfoDomainModel
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.LoadMoreMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.RefreshMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ShouldRefreshMoviesUseCase
import kotlinx.coroutines.flow.Flow

class MovieCatalogOrchestrator(
    private val observeAllMoviesUseCase: ObserveAllMoviesUseCase,
    private val refreshMoviesUseCase: RefreshMoviesUseCase,
    private val loadMoreMoviesUseCase: LoadMoreMoviesUseCase,
    private val shouldRefreshMoviesUseCase: ShouldRefreshMoviesUseCase
) {
    fun observeMovies(): Flow<AppResult<ObserveAllMoviesSuccess, AppError>> =
        observeAllMoviesUseCase()

    suspend fun fetchMovies(language: String): AppResult<Unit, AppError> =
        refreshMoviesUseCase(language)

    suspend fun loadMoreMovies(
        language: String,
        page: Int
    ): AppResult<PaginationInfoDomainModel, AppError> =
        loadMoreMoviesUseCase(language, page)

    suspend fun shouldRefreshMovies() = shouldRefreshMoviesUseCase()
}
