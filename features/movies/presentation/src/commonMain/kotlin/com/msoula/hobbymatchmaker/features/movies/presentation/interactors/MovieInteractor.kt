package com.msoula.hobbymatchmaker.features.movies.presentation.interactors

import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.LogOutUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.movies.domain.models.PaginationInfo
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.CheckMovieSynopsisValueUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.LoadMoreMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveAllMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.RefreshMoviesUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ShouldRefreshMoviesUseCase
import com.msoula.hobbymatchmaker.features.social.domain.models.MovieMatchResult
import com.msoula.hobbymatchmaker.features.social.domain.useCases.CheckMovieMatchUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.GetUserAvatarUrlUseCase
import kotlinx.coroutines.flow.Flow

class MovieInteractor(
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    private val observeAllMoviesUseCase: ObserveAllMoviesUseCase,
    private val refreshMoviesUseCase: RefreshMoviesUseCase,
    private val fetchFirebaseUserInfo: FetchFirebaseUserInfo,
    private val logOutUseCase: LogOutUseCase,
    private val checkMovieSynopsisValueUseCase: CheckMovieSynopsisValueUseCase,
    private val shouldRefreshMoviesUseCase: ShouldRefreshMoviesUseCase,
    private val loadMoreMoviesUseCase: LoadMoreMoviesUseCase,
    private val checkMovieMatchUseCase: CheckMovieMatchUseCase,
    private val getUserAvatarUrlUseCase: GetUserAvatarUrlUseCase,
    private val connectivityChecker: NetworkConnectivityChecker
) {
    fun observeMovies(): Flow<AppResult<ObserveAllMoviesSuccess, AppError>> =
        observeAllMoviesUseCase()

    suspend fun fetchMovies(language: String): AppResult<Unit, AppError> =
        refreshMoviesUseCase(language)

    suspend fun loadMoreMovies(language: String, page: Int): AppResult<PaginationInfo, AppError> =
        loadMoreMoviesUseCase(language, page)

    suspend fun shouldRefreshMovies() = shouldRefreshMoviesUseCase()

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

    suspend fun checkForMovieMatch(
        uid: String,
        movieId: Long
    ): AppResult<MovieMatchResult, AppError> = checkMovieMatchUseCase(
        uid, movieId
    )

    suspend fun getOwnerAvatarUrl(uid: String): String? {
        return when (val result = getUserAvatarUrlUseCase(uid)) {
            is AppResult.Success -> result.data
            is AppResult.Failure -> null
        }
    }
}
