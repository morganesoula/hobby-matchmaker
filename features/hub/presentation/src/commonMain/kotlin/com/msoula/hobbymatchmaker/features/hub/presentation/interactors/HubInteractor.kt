package com.msoula.hobbymatchmaker.features.hub.presentation.interactors

import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfoUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class HubInteractor(
    private val observeFavoriteMoviesUseCase: ObserveFavoriteMoviesUseCase,
    private val observeMatchedFriendsUseCase: ObserveMatchedFriendsUseCase,
    private val observeSharedFavoriteMoviesUseCase: ObserveSharedFavoriteMoviesUseCase,
    private val fetchFirebaseUserInfo: FetchFirebaseUserInfoUseCase,
    private val getSharedMovieIdsUseCase: GetSharedMovieIdsUseCase
) {
    private var currentUid: String? = null

    fun observeFavoriteMovies(): Flow<AppResult<FavoriteMoviesSuccess, AppError>> =
        observeFavoriteMoviesUseCase().map { movies ->
            if (movies.isEmpty()) {
                return@map AppResult.Success(FavoriteMoviesSuccess.Empty)
            }

            val uid = when (val result = getOrFetchUid()) {
                is AppResult.Success -> result.data
                is AppResult.Failure -> return@map AppResult.Failure(result.error)
            }

            return@map when (val result = uid?.let { getSharedMovieIdsUseCase(it) }) {
                is AppResult.Success -> AppResult.Success(
                    FavoriteMoviesSuccess.Success(
                        movies.map { movie ->
                            movie.toHubFavoriteMoviesUIModel(isShared = result.data.contains(movie.id))
                        }
                    )
                )

                // If null uid or no network, we still show movies without shared tag
                else -> AppResult.Success(
                    FavoriteMoviesSuccess.Success(
                        movies.map { movie -> movie.toHubFavoriteMoviesUIModel(isShared = false) }
                    ))
            }
        }

    suspend fun observeMatchedFriends(): Flow<AppResult<RecentMatchesSuccess, AppError>> {
        return when (val result = getOrFetchUid()) {
            is AppResult.Success -> {
                result.data?.let {
                    observeMatchedFriendsUseCase(it).map { data ->
                        when (data) {
                            is AppResult.Success -> {
                                if (data.data.isEmpty()) {
                                    AppResult.Success(RecentMatchesSuccess.Empty)
                                } else {
                                    AppResult.Success(RecentMatchesSuccess.Success(data.data.map { it.toHubRecentMatchesUIModel() }))
                                }
                            }

                            is AppResult.Failure -> return@map AppResult.Failure(data.error)
                        }

                    }
                } ?: flowOf(AppResult.Failure(AppError.Authentication.Unknown))
            }

            is AppResult.Failure -> return flowOf(AppResult.Failure(result.error))
        }
    }


    private suspend fun getOrFetchUid(): AppResult<String?, AppError> {
        if (currentUid != null) return AppResult.Success(currentUid)
        return getAuthenticatedUid()
    }

    private suspend fun getAuthenticatedUid(): AppResult<String?, AppError> =
        when (val result = fetchFirebaseUserInfo()) {
            is AppResult.Success -> when (val authState = result.data) {
                is AuthState.Authenticated -> {
                    currentUid = authState.user.uid
                    AppResult.Success(authState.user.uid)
                }

                else -> AppResult.Success(null)
            }

            is AppResult.Failure -> AppResult.Failure(result.error)
        }

    fun observeSharedFavoriteMovies(ids: List<Long>) = observeSharedFavoriteMoviesUseCase(ids)
        .map { movies ->
            movies.map {
                it.toHubFavoriteMoviesUIModel(isShared = true).toMovieCarouselItem()
            }
        }
}
