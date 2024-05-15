package com.msoula.hobbymatchmaker.core.common

import androidx.compose.runtime.Stable

@Stable
sealed interface AuthUiStateModel {
    data object NotConnected : AuthUiStateModel
    data object IsConnected : AuthUiStateModel
    data object CheckingState : AuthUiStateModel
}
