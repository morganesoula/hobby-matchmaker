package com.msoula.hobbymatchmaker.core.splashscreen.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.useCases.FetchFirebaseUserInfo
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ClearCurrentUserProfileUuidUseCase
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.model.SplashUiState
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SyncLocalFavoritesToCloudUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class SplashViewModel(
    private val observeIsConnectedUseCase: ObserveIsConnectedUseCase,
    private val fetchFirebaseUserInfo: FetchFirebaseUserInfo,
    private val clearCurrentUserProfileUuidUseCase: ClearCurrentUserProfileUuidUseCase,
    syncLocalFavoritesToCloudUseCase: Lazy<SyncLocalFavoritesToCloudUseCase>
) : ViewModel() {
    private val syncLocalFavoritesToCloudUseCase by syncLocalFavoritesToCloudUseCase

    val state: StateFlow<SplashUiState>
        field = MutableStateFlow<SplashUiState>(SplashUiState.Loading)

    init {
        viewModelScope.launch {
            try {
                val isLocallyConnected = withTimeout(3_000) { observeIsConnectedUseCase().first() }

                delay(1_000)

                if (isLocallyConnected) {
                    val isFirebaseSessionValid = validateFirebaseSession()

                    if (isFirebaseSessionValid) {
                        state.update { SplashUiState.GoToMovies }

                        launch {
                            runCatching { syncLocalFavoritesToCloudUseCase() }
                                .onFailure { Logger.w("Splash sync failed - ${it.message}") }
                        }
                    } else {
                        clearCurrentUserProfileUuidUseCase()
                        state.update { SplashUiState.GoToAuth }
                    }
                } else {
                    state.update { SplashUiState.GoToAuth }
                }
            } catch (t: Throwable) {
                state.update { SplashUiState.Error(t.message ?: "Unknown error") }
            }
        }
    }

    private suspend fun validateFirebaseSession(): Boolean {
        return when (val result = fetchFirebaseUserInfo()) {
            is AppResult.Success -> result.data is AuthState.Authenticated
            is AppResult.Failure -> {
                Logger.w("Firebase session validation failed: ${result.error}")
                false
            }
        }
    }
}
