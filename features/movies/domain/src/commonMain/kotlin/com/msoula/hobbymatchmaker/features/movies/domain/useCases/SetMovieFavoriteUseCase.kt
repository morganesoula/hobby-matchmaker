package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class SetMovieFavoriteUseCase(
    private val movieRepository: MovieRepository,
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(
        uuidUser: String,
        id: Long,
        isFavorite: Boolean
    ): R<Unit, AppError> {
        movieRepository.updateMovieFavoriteLocal(id, isFavorite)

        val isLoggedIn = authenticationRepository.fetchFirebaseUserInfo() != null

        return if (isLoggedIn)
            movieRepository.updateMovieFavoriteRemote(uuidUser, id, isFavorite)
        else R.Success(Unit)
    }
}
