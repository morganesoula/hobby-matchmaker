package com.msoula.hobbymatchmaker.core.splashscreen.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthenticatedUser
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfoUseCase
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.design.util.ErrorMessageMapper
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ClearCurrentUserProfileUuidUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.RemoveLegacyUserEmailUseCase
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.model.SplashUiState
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SyncLocalFavoritesToCloudUseCase
import com.msoula.hobbymatchmaker.features.profile.domain.useCases.HydrateUserProfileUseCase
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class SplashViewModel(
    private val observeIsConnectedUseCase: ObserveIsConnectedUseCase,
    private val fetchFirebaseUserInfo: FetchFirebaseUserInfoUseCase,
    private val clearCurrentUserProfileUuidUseCase: ClearCurrentUserProfileUuidUseCase,
    private val hydrateUserProfileUseCase: HydrateUserProfileUseCase,
    private val removeLegacyUserEmailUseCase: RemoveLegacyUserEmailUseCase,
    syncLocalFavoritesToCloudUseCase: Lazy<SyncLocalFavoritesToCloudUseCase>,
    private val defaultErrorMessageMapper: ErrorMessageMapper
) : ViewModel() {
    private val syncLocalFavoritesToCloudUseCase by syncLocalFavoritesToCloudUseCase

    val state: StateFlow<SplashUiState>
        field = MutableStateFlow<SplashUiState>(SplashUiState.Loading)

    init {
        viewModelScope.launch {
            try {
                val isLocallyConnected = withTimeout(3_000) { observeIsConnectedUseCase().first() }
                val navigationTarget = if (isLocallyConnected) {
                    val authenticatedUser = fetchAuthenticatedUser()
                    if (authenticatedUser != null) {
                        launch {
                            runCatching { syncLocalFavoritesToCloudUseCase() }
                                .onFailure { Logger.w("Splash sync failed: ${it.message}") }
                        }
                        launch {
                            removeLegacyUserEmailUseCase(authenticatedUser.uid)
                                .onFailure { Logger.w("Legacy email cleanup failed: $it") }
                        }
                        if (needsPseudo(authenticatedUser)) SplashUiState.GoToPickPseudo
                        else SplashUiState.GoToMovies
                    } else {
                        clearCurrentUserProfileUuidUseCase()
                        SplashUiState.GoToAuth
                    }
                } else {
                    SplashUiState.GoToAuth
                }

                delay(1_000)
                state.update { navigationTarget }
            } catch (e: TimeoutCancellationException) {
                state.update { SplashUiState.GoToAuth }
            } catch (e: Exception) {
                Logger.e("Splash screen error: $e")
                state.update { SplashUiState.Error(defaultErrorMessageMapper.toUIText(AppError.Authentication.Unknown)) }
            }
        }
    }

    // Only a confirmed missing pseudo gates the user; a failed lookup (e.g. offline) lets them in
    private suspend fun needsPseudo(user: AuthenticatedUser): Boolean =
        when (val result = hydrateUserProfileUseCase(user.uid, user.displayName.orEmpty().substringBefore(" "))) {
            is AppResult.Success -> !result.data
            is AppResult.Failure -> false
        }

    private suspend fun fetchAuthenticatedUser(): AuthenticatedUser? {
        return when (val result = fetchFirebaseUserInfo()) {
            is AppResult.Success -> (result.data as? AuthState.Authenticated)?.user
            is AppResult.Failure -> {
                Logger.w("Firebase session validation failed: ${result.error}")
                null
            }
        }
    }
}
