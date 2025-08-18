package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository

class SetMovieFavoriteUseCase(
    private val movieRepository: MovieRepository,
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(uuidUser: String, id: Long, isFavorite: Boolean) {
        movieRepository.updateMovieFavoriteLocal(id, isFavorite)

        if (authenticationRepository.fetchFirebaseUserInfo() != null) {
            try {
                movieRepository.updateMovieFavoriteRemote(uuidUser, id, isFavorite)
            } catch (_: Exception) {
                Logger.d("No information known - not sending data remotely")
            }
        }
    }
}
