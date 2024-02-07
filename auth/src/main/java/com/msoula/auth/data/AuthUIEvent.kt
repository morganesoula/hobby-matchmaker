package com.msoula.auth.data

sealed interface AuthUIEvent {
    data class OnEmailChanged(val email: String) : AuthUIEvent

    data class OnEmailResetChanged(val emailReset: String) : AuthUIEvent

    data class OnPasswordChanged(val password: String) : AuthUIEvent

    data class OnFirstNameChanged(val firstName: String) : AuthUIEvent

    data class OnLastNameChanged(val lastName: String) : AuthUIEvent

    data object HideForgotPasswordDialog : AuthUIEvent

    data object OnForgotPasswordClicked : AuthUIEvent

    data object OnResetPasswordConfirmed : AuthUIEvent

    data object OnSignUp : AuthUIEvent

    data object OnLogIn : AuthUIEvent
}
