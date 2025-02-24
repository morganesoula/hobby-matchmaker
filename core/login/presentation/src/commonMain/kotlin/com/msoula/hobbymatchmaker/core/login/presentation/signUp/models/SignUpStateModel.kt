package com.msoula.hobbymatchmaker.core.login.presentation.signUp.models

import kotlinx.serialization.Serializable

@Serializable
data class SignUpStateModel(
    val firstName: String = "",
    val email: String = "",
    val password: String = "",
    val submit: Boolean = false,
    val signUpError: String? = null
)
