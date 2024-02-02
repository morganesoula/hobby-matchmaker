package com.msoula.auth.domain.repository

import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.CoroutineScope

typealias LogOutResponse = Response<Boolean>
typealias SignUpResponse = Response<AuthResult>
typealias LoginResponse = Response<AuthResult>
typealias ResetEmailResponse = Response<Boolean>

sealed class Response<out T> {
    data class Success<out T>(val data: T?) : Response<T>()
    data class Failure(val exception: Exception) : Response<Nothing>()
}

interface AuthRepository {
    fun getAuthState(viewModelScope: CoroutineScope): Boolean
    suspend fun logOut(): LogOutResponse
    suspend fun signUp(email: String, password: String): SignUpResponse
    suspend fun loginWithEmailAndPassword(email: String, password: String): LoginResponse
    suspend fun resetPassword(email: String): ResetEmailResponse
}