package com.msoula.auth.domain.repository

import com.google.firebase.auth.AuthResult
import com.msoula.network.ResponseHMM

typealias LogOutResponse = ResponseHMM<Boolean>
typealias SignUpResponse = ResponseHMM<AuthResult>
typealias LoginResponse = ResponseHMM<AuthResult>
typealias ResetEmailResponse = ResponseHMM<Boolean>

interface AuthRepository {
    fun getAuthState(): Boolean

    suspend fun logOut(): LogOutResponse

    suspend fun signUp(
        email: String,
        password: String,
    ): SignUpResponse

    suspend fun loginWithEmailAndPassword(
        email: String,
        password: String,
    ): LoginResponse

    suspend fun resetPassword(email: String): ResetEmailResponse
}
