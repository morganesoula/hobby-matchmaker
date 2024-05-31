package com.msoula.hobbymatchmaker.core.authentication.domain.data_sources

import com.google.firebase.auth.AuthCredential
import com.msoula.hobbymatchmaker.core.authentication.domain.models.UserDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import kotlinx.coroutines.flow.Flow

interface AuthenticationRemoteDataSource {
    fun authenticationSignOut()
    fun loginManagerSignOut()
    fun getCurrentAuthenticationUser(): Flow<UserDomainModel?>
    suspend fun signInWithCredentials(credential: AuthCredential): Result<Boolean>
    suspend fun oneTapClientSignOut()
    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<Boolean>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Boolean>
    suspend fun resetPassword(email: String): Result<Boolean>
}
