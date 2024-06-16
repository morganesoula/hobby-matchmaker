package com.msoula.hobbymatchmaker.core.login.presentation.models

sealed interface AuthenticationUIEvent {
    data class OnEmailChanged(val email: String) : AuthenticationUIEvent

    data class OnEmailResetChanged(val emailReset: String) : AuthenticationUIEvent

    data class OnPasswordChanged(val password: String) : AuthenticationUIEvent

    data class OnFirstNameChanged(val firstName: String) : AuthenticationUIEvent

    data object HideForgotPasswordDialog : AuthenticationUIEvent

    data object OnForgotPasswordClicked : AuthenticationUIEvent

    data object OnResetPasswordConfirmed : AuthenticationUIEvent

    data object OnSignUp : AuthenticationUIEvent

    data object OnSignIn : AuthenticationUIEvent
}
