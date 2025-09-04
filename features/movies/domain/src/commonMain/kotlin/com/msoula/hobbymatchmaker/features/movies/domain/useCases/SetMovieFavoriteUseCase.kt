package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class SetMovieFavoriteUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(
        uuidUser: String,
        id: Long,
        isFavorite: Boolean
    ): AppResult<Unit, AppError> {
        movieRepository.updateMovieFavoriteLocal(id, isFavorite)
        return if (uuidUser.isNotBlank()) movieRepository.updateMovieFavoriteRemote(
            uuidUser,
            id,
            isFavorite
        ) else AppResult.Success(Unit)
    }
}
