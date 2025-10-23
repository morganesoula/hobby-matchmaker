package com.msoula.hobbymatchmaker.core.splashscreen.presentation.model

sealed interface SplashUiState {
    data object Loading: SplashUiState
    data object Authenticated : SplashUiState
    data object NotAuthenticated : SplashUiState
    data class Error(val message: String) : SplashUiState
}
