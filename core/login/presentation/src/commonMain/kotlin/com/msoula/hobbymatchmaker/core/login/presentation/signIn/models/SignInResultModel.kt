package com.msoula.hobbymatchmaker.core.login.presentation.signIn.models

data class UserData(
    val userId: String,
    val userName: String?,
    val profilePictureUrl: String? = null,
)
