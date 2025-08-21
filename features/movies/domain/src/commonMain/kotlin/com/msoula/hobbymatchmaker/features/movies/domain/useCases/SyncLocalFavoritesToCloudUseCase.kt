package com.msoula.hobbymatchmaker.features.movies.domain.useCases

import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.R
import com.msoula.hobbymatchmaker.features.movies.domain.repositories.MovieRepository
import kotlin.coroutines.cancellation.CancellationException

class SyncLocalFavoritesToCloudUseCase(
    private val movieRepository: MovieRepository,
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke() {
        val uid = authenticationRepository.fetchFirebaseUserInfo()?.uid ?: return

        when (val localIds = movieRepository.getFavoriteLocalMovieIds()) {
            is R.Success -> {
                try {
                    movieRepository.syncUserFavoritesRemote(uid, localIds.data)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Logger.w("Favorite remote movie push failed, will sync later — ${e.message}")
                }
            }

            is R.Failure -> Logger.w("Skip sync favorites: ${localIds.error}")
        }
    }
}
