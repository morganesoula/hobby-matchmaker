package com.msoula.hobbymatchmaker.core.login.presentation.sign_in.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignInFormStateModel(
    val email: String = "",
    val emailReset: String = "",
    val password: String = "",
    val submit: Boolean = false,
    val submitEmailReset: Boolean = false,
    val logInError: String? = null
) : Parcelable
