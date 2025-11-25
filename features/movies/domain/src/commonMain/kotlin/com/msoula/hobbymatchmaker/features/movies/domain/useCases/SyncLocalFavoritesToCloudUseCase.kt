package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SyncLocalFavoritesToCloudUseCase(
    private val movieRepository: MovieRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke() = withContext(dispatcher) {
        val uid = when (val auth = authenticationRepository.fetchUserInfo()) {
            is AppResult.Failure -> {
                Logger.w("Auth state fetch failed: ${auth.error}")
                return@withContext
            }

            is AppResult.Success -> when (val data = auth.data) {
                AuthState.SignedOut -> {
                    Logger.w("User signed out, skip sync")
                    return@withContext
                }

                is AuthState.Authenticated -> data.user.uid
            }
        }

        when (val favorites = movieRepository.getFavoriteLocalMovieIds()) {
            is AppResult.Failure -> Logger.w("Skip sync favorites: ${favorites.error}")
            is AppResult.Success -> {
                when (val push =
                    movieRepository.syncUserFavoritesRemote(uid, favorites.data)) {
                    is AppResult.Failure -> Logger.w("Favorite remote movie push failed, will sync later — $push")
                    is AppResult.Success -> Unit
                }
            }
        }
    }
}
