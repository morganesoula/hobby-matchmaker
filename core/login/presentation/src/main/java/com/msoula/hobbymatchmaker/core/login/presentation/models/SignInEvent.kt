package com.msoula.hobbymatchmaker.core.login.presentation.models

sealed interface SignInEvent {
    data object Loading : SignInEvent
    data class Error(val message: String) : SignInEvent
    data object Success : SignInEvent
    data object Idle : SignInEvent
}
