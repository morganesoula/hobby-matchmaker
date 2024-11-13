package com.msoula.hobbymatchmaker.core.login.presentation.models

sealed interface ResetPasswordEvent {
    data object Loading : ResetPasswordEvent
    data class Error(val message: String) : ResetPasswordEvent
    data object Success : ResetPasswordEvent
    data object Idle : ResetPasswordEvent
}
