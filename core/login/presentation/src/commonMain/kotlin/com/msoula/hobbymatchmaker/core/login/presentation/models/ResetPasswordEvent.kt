package com.msoula.hobbymatchmaker.core.login.presentation.models

sealed interface ResetPasswordEvent {
    data object Loading : ResetPasswordEvent
    data object Idle : ResetPasswordEvent
}
