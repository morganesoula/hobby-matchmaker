package com.msoula.hobbymatchmaker.features.hub.presentation.interactors

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.hub.presentation.mappers.toHubFavoriteMoviesUIModel
import com.msoula.hobbymatchmaker.features.hub.presentation.models.HubFavoriteMoviesUIModel
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveFavoriteMoviesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HubInteractor(
    private val observeFavoriteMoviesUseCase: ObserveFavoriteMoviesUseCase
) {
    suspend fun observeFavoriteMovies(): Flow<AppResult<FavoriteMoviesSuccess, AppError>> =
        observeFavoriteMoviesUseCase().map { movies ->
            if (movies.isNotEmpty()) {
                AppResult.Success(
                    FavoriteMoviesSuccess.Success(
                        movies.map { movie -> movie.toHubFavoriteMoviesUIModel() }
                    ))
            } else {
                AppResult.Success(FavoriteMoviesSuccess.Empty)
            }
        }
}

sealed interface FavoriteMoviesSuccess {
    data object Empty : FavoriteMoviesSuccess
    data class Success(val movies: List<HubFavoriteMoviesUIModel>) : FavoriteMoviesSuccess
}
