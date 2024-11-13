package com.msoula.hobbymatchmaker.core.login.presentation.models

sealed interface AuthenticationEvent {
    val message: String

    data class OnFacebookFailedConnection(override val message: String) : AuthenticationEvent
    data class OnGoogleFailedConnection(override val message: String) : AuthenticationEvent
    data class OnSocialMediaFailedConnection(override val message: String) : AuthenticationEvent
    data class OnResetPasswordFailed(override val message: String) : AuthenticationEvent
    data object OnResetPasswordSuccess : AuthenticationEvent {
        override val message: String
            get() = "Successful password reset"
    }

    data object OnSignInSuccess : AuthenticationEvent {
        override val message: String
            get() = "successful sign in"
    }

    data object OnSignUpClicked : AuthenticationEvent {
        override val message: String
            get() = "redirecting to sign up screen"
    }

    data object OnSignUpSuccess : AuthenticationEvent {
        override val message: String
            get() = "successful sign up"
    }

    data object Loading : AuthenticationEvent {
        override val message: String
            get() = "loading"
    }
}
