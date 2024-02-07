package com.msoula.auth.data

data class SignUpRegistrationState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val submit: Boolean = false,
    // Error section
    val signUpError: String? = null,
)
