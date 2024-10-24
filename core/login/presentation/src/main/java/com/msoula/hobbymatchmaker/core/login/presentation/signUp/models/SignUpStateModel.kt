package com.msoula.hobbymatchmaker.core.login.presentation.signUp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpStateModel(
    val firstName: String = "",
    val email: String = "",
    val password: String = "",
    val submit: Boolean = false,
    val signUpError: String? = null
) : Parcelable
