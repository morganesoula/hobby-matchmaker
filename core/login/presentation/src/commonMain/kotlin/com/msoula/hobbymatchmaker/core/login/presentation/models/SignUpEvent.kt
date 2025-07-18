package com.msoula.hobbymatchmaker.core.login.presentation.models

sealed interface SignUpEvent {
    data object Loading : SignUpEvent
    data class Error(val message: String) : SignUpEvent
    data object Success : SignUpEvent
    data object Idle : SignUpEvent
}
