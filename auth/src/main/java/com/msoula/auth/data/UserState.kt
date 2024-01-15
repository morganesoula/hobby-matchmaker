package com.msoula.auth.data

import com.google.firebase.auth.FirebaseUser

data class UserState(
    val name: FirebaseUser? = null,
    val email: String = "",
    val password: String = "",
    val connected: Boolean = false
)
