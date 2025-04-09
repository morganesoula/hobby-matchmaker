package com.msoula.hobbymatchmaker.core.login.presentation.models

import org.jetbrains.compose.resources.StringResource

sealed interface SignUpEvent {
    data object Loading : SignUpEvent
    data class Error(val message: StringResource?) : SignUpEvent
    data object Success : SignUpEvent
    data object Idle : SignUpEvent
}
