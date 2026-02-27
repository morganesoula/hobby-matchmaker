package com.msoula.hobbymatchmaker.features.moviedetail.presentation.interactors

import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfoUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.FetchMovieTrailerUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieDetailUseCase
import com.msoula.hobbymatchmaker.features.moviedetail.domain.useCases.ObserveMovieSuccess
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MatchingMemberUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.MovieDetailUiModel
import com.msoula.hobbymatchmaker.features.moviedetail.presentation.models.toMovieDetailUiModel
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SetMovieFavoriteUseCase
import com.msoula.hobbymatchmaker.features.social.domain.models.MovieMatchResult
import com.msoula.hobbymatchmaker.features.social.domain.useCases.CheckMovieMatchUseCase
import com.msoula.hobbymatchmaker.features.social.domain.useCases.SyncFavoriteToCircleUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieDetailInteractor(
    private val observeMovieDetailUseCase: ObserveMovieDetailUseCase,
    private val fetchMovieTrailerUseCase: FetchMovieTrailerUseCase,
    private val setMovieFavoriteUseCase: SetMovieFavoriteUseCase,
    private val fetchFirebaseUserInfo: FetchFirebaseUserInfoUseCase,
    private val checkMovieMatchUseCase: CheckMovieMatchUseCase,
    private val syncFavoriteToCircleUseCase: SyncFavoriteToCircleUseCase
) {
    fun observeMovieDetail(
        movieId: Long,
        language: String
    ): Flow<AppResult<MovieSuccess, AppError>> =
        observeMovieDetailUseCase(movieId, language).map { result ->
            when (result) {
                is AppResult.Success -> {
                    when (val data = result.data) {
                        is ObserveMovieSuccess.Success -> AppResult.Success(
                            MovieSuccess.Success(data.data.toMovieDetailUiModel())
                        )

                        is ObserveMovieSuccess.DataLoadedInDB -> AppResult.Success(MovieSuccess.Loaded)
                    }
                }

                is AppResult.Failure -> AppResult.Failure(result.error)
            }
        }

    suspend fun fetchTrailer(
        movieId: Long,
        language: String
    ) = fetchMovieTrailerUseCase(movieId, language).mapSuccess { it.videoURI }

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

                syncFavoriteToCircleUseCase(uid, movieId, isFavorite)
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
        checkMovieMatchUseCase(uid, movieId).mapSuccess { result ->
            when (result) {
                is MovieMatchResult.Match -> MovieMatchUiSuccess(result.matchingMemberDomainModels.map {
                    MatchingMemberUiModel(
                        name = it.displayName, avatarUrl = it.avatarUrl
                    )
                })

                else -> Unit
            }
        }
}

sealed interface MovieSuccess {
    data class Success(val data: MovieDetailUiModel) : MovieSuccess
    data object Loaded : MovieSuccess
}

class MovieMatchUiSuccess(val matchingMembers: List<MatchingMemberUiModel>)
