package com.msoula.auth.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpRegistrationState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val submit: Boolean = false,
    // Error section
    val signUpError: String? = null,
) : Parcelable
