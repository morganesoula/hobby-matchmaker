package com.msoula.auth.data

data class SignInResult(
    val data: UserData?,
    val errorMessage: String? = null,
)

data class UserData(
    val userId: String,
    val userName: String?,
    val profilePictureUrl: String? = null,
)
