package com.msoula.hobbymatchmaker.core.login.presentation.signIn.models

import kotlinx.serialization.Serializable

@Serializable
data class SignInFormStateModel(
    val email: String = "",
    val emailReset: String = "",
    val password: String = "",
    val submit: Boolean = false,
    val submitEmailReset: Boolean = false
)
