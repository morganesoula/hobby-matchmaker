package com.msoula.hobbymatchmaker.presentation.models

sealed interface LogOutState {
    data object Idle : LogOutState
    data object Success : LogOutState
    data class Error(val message: String) : LogOutState
}
