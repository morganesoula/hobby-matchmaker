package com.msoula.hobbymatchmaker.core.login.presentation.models

sealed interface AuthenticationEvent {
    val message: String

    data class OnFacebookFailedConnection(override val message: String) : AuthenticationEvent
    data class OnGoogleFailedConnection(override val message: String) : AuthenticationEvent
    data class OnResetPasswordFailed(override val message: String) : AuthenticationEvent
}
