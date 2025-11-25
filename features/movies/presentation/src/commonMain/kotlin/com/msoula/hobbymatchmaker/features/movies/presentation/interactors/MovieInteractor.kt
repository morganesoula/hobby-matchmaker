package com.msoula.hobbymatchmaker.features.movies.presentation.interactors

import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.CheckMovieSynopsisValueUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase

class MovieInteractor(
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    private val observeAllMoviesUseCase: ObserveAllMoviesUseCase,
    private val fetchFirebaseUserInfo: FetchFirebaseUserInfo,
    private val logOutUseCase: LogOutUseCase,
    private val checkMovieSynopsisValueUseCase: CheckMovieSynopsisValueUseCase,
    private val connectivityChecker: NetworkConnectivityChecker
) {

    fun observeMovies(language: String) = observeAllMoviesUseCase(language)

    suspend fun logOut() = logOutUseCase()

    suspend fun toggleFavorite(movieId: Long, isFavorite: Boolean): AppResult<Unit, AppError> {
        return when (val authResult = fetchFirebaseUserInfo()) {
            is AppResult.Failure -> AppResult.Failure(authResult.error)
            is AppResult.Success -> {
                val uid = when (val state = authResult.data) {
                    is AuthState.Authenticated -> state.user.uid
                    AuthState.SignedOut -> return AppResult.Failure(
                        AppError.Authentication.InvalidCredentials
                    )
                }
                setMovieFavoriteUseCase(uid, movieId, isFavorite)
            }
        }
    }

    suspend fun canAccessMovieDetail(movieId: Long): Boolean {
        val local = when (val localResult = checkMovieSynopsisValueUseCase(movieId)) {
            is AppResult.Success -> localResult.data
            is AppResult.Failure -> false
        }

        val online = connectivityChecker.hasActiveConnection()

        return local || online
    }
}
