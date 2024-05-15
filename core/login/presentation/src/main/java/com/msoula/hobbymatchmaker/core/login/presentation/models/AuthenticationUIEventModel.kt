package com.msoula.hobbymatchmaker.core.login.presentation.models

sealed interface AuthenticationUIEventModel {
    data class OnEmailChanged(val email: String) : AuthenticationUIEventModel

    data class OnEmailResetChanged(val emailReset: String) : AuthenticationUIEventModel

    data class OnPasswordChanged(val password: String) : AuthenticationUIEventModel

    data class OnFirstNameChanged(val firstName: String) : AuthenticationUIEventModel

    data class OnLastNameChanged(val lastName: String) : AuthenticationUIEventModel

    data object HideForgotPasswordDialog : AuthenticationUIEventModel

    data object OnForgotPasswordClicked : AuthenticationUIEventModel

    data object OnResetPasswordConfirmed : AuthenticationUIEventModel

    data object OnSignUp : AuthenticationUIEventModel

    data object OnLogIn : AuthenticationUIEventModel
}
