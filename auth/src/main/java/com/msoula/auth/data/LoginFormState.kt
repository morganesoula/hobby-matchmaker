package com.msoula.auth.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginFormState(
    val email: String = "",
    val emailReset: String = "",
    val password: String = "",
    val submit: Boolean = false,
    val submitEmailReset: Boolean = false,
    // Error section
    val logInError: String? = null,
) : Parcelable
