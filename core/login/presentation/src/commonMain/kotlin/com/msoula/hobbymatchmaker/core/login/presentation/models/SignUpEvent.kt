package com.msoula.hobbymatchmaker.core.login.presentation.models

sealed interface SignUpEvent {
    data object Loading : SignUpEvent
    data object Idle : SignUpEvent
}
