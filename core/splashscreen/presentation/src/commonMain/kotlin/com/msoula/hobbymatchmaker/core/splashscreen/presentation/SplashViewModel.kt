package com.msoula.hobbymatchmaker.core.splashscreen.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.model.SplashUiState
import com.msoula.hobbymatchmaker.features.movies.domain.useCases.SyncLocalFavoritesToCloudUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class SplashViewModel(
    private val observeIsConnectedUseCase: ObserveIsConnectedUseCase,
    private val syncLocalFavoritesToCloudUseCase: SyncLocalFavoritesToCloudUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val shouldSkipAuth = withTimeout(3_000) { observeIsConnectedUseCase().first() }

                delay(1_000)

                if (shouldSkipAuth) {
                    runCatching { syncLocalFavoritesToCloudUseCase() }
                        .onFailure { Logger.w("Splash sync failed - ${it.message}") }
                    _state.update { SplashUiState.GoToMovies }
                } else {
                    _state.update { SplashUiState.GoToAuth }
                }
            } catch (t: Throwable) {
                _state.update { SplashUiState.Error(t.message ?: "Unknown error") }
            }
        }
    }
}
