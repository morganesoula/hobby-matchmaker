package com.msoula.auth.presentation

sealed interface AuthFormEvent {

    data class OnEmailChanged(val email: String) : AuthFormEvent
    data class OnPasswordChanged(val password: String) : AuthFormEvent
    data object OnSubmitAuthForm : AuthFormEvent
}