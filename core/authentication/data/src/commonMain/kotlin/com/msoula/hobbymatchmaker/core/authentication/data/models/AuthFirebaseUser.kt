package com.msoula.hobbymatchmaker.core.authentication.data.models

data class AuthFirebaseUser(
    val uid: String = "",
    val email: String? = null,
    val providers: List<String>,
    val signInProvider: String? = null
) {
    companion object {
        const val DEFAULT_EMAIL = ""
    }
}
