package com.msoula.hobbymatchmaker.core.authentication.domain.data_sources

import com.google.firebase.auth.AuthCredential
import com.msoula.hobbymatchmaker.core.common.Result

interface AuthenticationRemoteDataSource {
    fun authenticationSignOut()
    fun loginManagerSignOut()
    suspend fun signInWithCredentials(credential: AuthCredential): Result<Boolean>
    suspend fun credentialManagerLogOut()
    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<Boolean>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Boolean>
    suspend fun resetPassword(email: String): Result<Boolean>
}
