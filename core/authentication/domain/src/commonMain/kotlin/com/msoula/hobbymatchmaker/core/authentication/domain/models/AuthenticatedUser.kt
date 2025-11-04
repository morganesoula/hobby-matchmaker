package com.msoula.hobbymatchmaker.core.authentication.domain.models

data class AuthenticatedUser(
    val uid: String,
    val email: String?,
    val providers: List<String>
)
