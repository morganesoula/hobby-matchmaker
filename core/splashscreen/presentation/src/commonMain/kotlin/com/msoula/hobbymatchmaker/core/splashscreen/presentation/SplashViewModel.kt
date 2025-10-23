package com.msoula.hobbymatchmaker.core.splashscreen.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.model.SplashUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class SplashViewModel(
    private val observeIsConnectedUseCase: ObserveIsConnectedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val state = _state.asStateFlow()

    fun start() {
        viewModelScope.launch {
            try {
                val isConnected = withTimeout(3_000) { observeIsConnectedUseCase().first() }
                delay(1_500)

                _state.update { if (isConnected) SplashUiState.Authenticated else SplashUiState.NotAuthenticated }
            } catch (t: Throwable) {
                _state.update { SplashUiState.Error(t.message ?: "Unknown error") }
            }
        }
    }
}
