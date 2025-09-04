package com.msoula.hobbymatchmaker.core.login.presentation.models

sealed interface SignInEvent {
    data object Loading : SignInEvent
    data object Idle : SignInEvent
}
