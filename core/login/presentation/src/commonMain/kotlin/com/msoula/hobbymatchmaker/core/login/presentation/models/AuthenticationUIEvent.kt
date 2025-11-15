package com.msoula.hobbymatchmaker.core.login.presentation.models

import dev.gitlive.firebase.auth.AuthCredential

sealed interface AuthenticationUIEvent {
    data class OnEmailChanged(val email: String) : AuthenticationUIEvent

    data class OnEmailResetChanged(val emailReset: String) : AuthenticationUIEvent

    data class OnPasswordChanged(val password: String) : AuthenticationUIEvent

    data class OnFirstNameChanged(val firstName: String) : AuthenticationUIEvent

    data object OnGoogleButtonClicked : AuthenticationUIEvent

    data object OnAppleButtonClicked : AuthenticationUIEvent

    data class OnFacebookButtonClicked(val credential: AuthCredential) : AuthenticationUIEvent

    data class SaveDontAskGuestDialogValue(val dontAskGuestDialog: Boolean) : AuthenticationUIEvent
    data object SetAccountAsGuest : AuthenticationUIEvent

    data object OnResetPasswordConfirmed : AuthenticationUIEvent

    data object OnScreenChanged : AuthenticationUIEvent

    data object OnSignUp : AuthenticationUIEvent

    data object OnSignIn : AuthenticationUIEvent
}
