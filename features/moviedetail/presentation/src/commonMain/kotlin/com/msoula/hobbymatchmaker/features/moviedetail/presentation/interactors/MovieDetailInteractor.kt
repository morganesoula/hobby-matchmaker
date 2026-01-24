package com.msoula.hobbymatchmaker.features.moviedetail.presentation.interactors

import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ManageMovieTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.CheckMovieMatchUseCase

class MovieDetailInteractor(
    private val observeMovieDetailUseCase: ObserveMovieDetailUseCase,
    private val manageMovieTrailerUseCase: ManageMovieTrailerUseCase,
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    private val fetchFirebaseUserInfo: FetchFirebaseUserInfo,
    private val checkMovieMatchUseCase: CheckMovieMatchUseCase,
    private val connectivityChecker: NetworkConnectivityChecker
) {
    fun observeMovieDetail(movieId: Long, language: String) =
        observeMovieDetailUseCase(movieId, language)

    fun canPlayTrailerDirectly(
        isVideoUriKnown: Boolean
    ): Boolean = isVideoUriKnown && connectivityChecker.hasActiveConnection()

    suspend fun fetchTrailer(
        movieId: Long,
        language: String
    ) = manageMovieTrailerUseCase(movieId, language).mapSuccess { it.videoURI }

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

    suspend fun getAuthenticatedUid(): String? {
        return when (val authResult = fetchFirebaseUserInfo()) {
            is AppResult.Failure -> null
            is AppResult.Success -> {
                when (val state = authResult.data) {
                    is AuthState.Authenticated -> state.user.uid
                    AuthState.SignedOut -> null
                }
            }
        }
    }

    suspend fun checkForMovieMatch(uid: String, movieId: Long) =
        checkMovieMatchUseCase(uid, movieId)
}
