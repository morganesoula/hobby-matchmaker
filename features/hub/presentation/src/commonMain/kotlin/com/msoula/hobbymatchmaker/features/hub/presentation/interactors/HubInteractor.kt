package com.msoula.hobbymatchmaker.features.hub.presentation.interactors

import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfoUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.network.NetworkConnectivityChecker
import com.msoula.hobbymatchmaker.features.hub.domain.useCases.ObserveMatchedFriendsUseCase
import com.msoula.hobbymatchmaker.features.hub.domain.useCases.ObserveSharedFavoriteMoviesUseCase
import com.msoula.hobbymatchmaker.features.hub.presentation.mappers.toHubFavoriteMoviesUIModel
import com.msoula.hobbymatchmaker.features.hub.presentation.mappers.toHubRecentMatchesUIModel
import com.msoula.hobbymatchmaker.features.hub.presentation.mappers.toMovieCarouselItem
import com.msoula.hobbymatchmaker.features.hub.presentation.models.FavoriteMoviesSuccess
import com.msoula.hobbymatchmaker.features.hub.presentation.models.RecentMatchesSuccess
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.ObserveFavoriteMoviesUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.GetSharedMovieIdsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class HubInteractor(
    private val observeFavoriteMoviesUseCase: ObserveFavoriteMoviesUseCase,
    private val observeMatchedFriendsUseCase: ObserveMatchedFriendsUseCase,
    private val observeSharedFavoriteMoviesUseCase: ObserveSharedFavoriteMoviesUseCase,
    private val fetchFirebaseUserInfo: FetchFirebaseUserInfoUseCase,
    private val getSharedMovieIdsUseCase: GetSharedMovieIdsUseCase,
    private val connectivityChecker: NetworkConnectivityChecker
) {
    fun observeFavoriteMovies(): Flow<AppResult<FavoriteMoviesSuccess, AppError>> =
        observeFavoriteMoviesUseCase().map { movies ->
            if (movies.isEmpty()) {
                return@map AppResult.Success(FavoriteMoviesSuccess.Empty)
            }

            val uid = getAuthenticatedUid() ?: return@map AppResult.Success(
                FavoriteMoviesSuccess.Success(
                    movies.map { it.toHubFavoriteMoviesUIModel(isShared = false) }
                )
            )

            return@map when (val result = getSharedMovieIdsUseCase(uid)) {
                is AppResult.Success -> AppResult.Success(
                    FavoriteMoviesSuccess.Success(
                        movies.map { movie ->
                            movie.toHubFavoriteMoviesUIModel(isShared = result.data.contains(movie.id))
                        }
                    )
                )

                is AppResult.Failure -> AppResult.Success(
                    FavoriteMoviesSuccess.Success(
                        movies.map { movie -> movie.toHubFavoriteMoviesUIModel(isShared = false) }
                    ))
            }
        }

    fun observeMatchedFriends(): Flow<AppResult<RecentMatchesSuccess, AppError>> = flow {
        val result = getAuthenticatedUid()
            ?.let { observeMatchedFriendsUseCase(it) }
            ?: AppResult.Failure(AppError.Authentication.Unknown)

        emit(
            result.mapSuccess { friends ->
                if (friends.isEmpty()) {
                    RecentMatchesSuccess.Empty
                } else {
                    RecentMatchesSuccess.Success(
                        friends.map { it.toHubRecentMatchesUIModel() }
                    )
                }
            }
        )
    }

    private suspend fun getAuthenticatedUid(): String? {
        if (!connectivityChecker.hasActiveConnection()) return null

        return when (val result = fetchFirebaseUserInfo()) {
            is AppResult.Success -> when (val authState = result.data) {
                is AuthState.Authenticated -> authState.user.uid
                else -> null
            }

            is AppResult.Failure -> null
        }
    }

    fun observeSharedFavoriteMovies(ids: List<Long>) = observeSharedFavoriteMoviesUseCase(ids)
        .map { movies ->
            movies.map {
                it.toHubFavoriteMoviesUIModel(isShared = true).toMovieCarouselItem()
            }
        }
}
