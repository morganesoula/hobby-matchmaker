package com.msoula.hobbymatchmaker.core.splashscreen.presentation.model

import androidx.compose.runtime.Immutable
import com.msoula.hobbymatchmaker.core.design.util.UIText

@Immutable
sealed interface SplashUiState {
    data object Loading: SplashUiState
    data object GoToMovies : SplashUiState
    data object GoToAuth : SplashUiState
    data class Error(val message: UIText) : SplashUiState
}
