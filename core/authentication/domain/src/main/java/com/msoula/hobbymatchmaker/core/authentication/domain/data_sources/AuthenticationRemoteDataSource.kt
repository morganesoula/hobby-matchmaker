package com.msoula.hobbymatchmaker.core.authentication.domain.data_sources

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.common.Result

interface AuthenticationRemoteDataSource {
    suspend fun authenticationSignOut()
    suspend fun signInWithCredentials(credential: AuthCredential): Result<AuthResult>
    suspend fun linkWithCredential(credential: AuthCredential): Result<AuthResult?>
    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<String>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<String>
    suspend fun resetPassword(email: String): Result<Boolean>
    suspend fun getUserUid(): String?
    suspend fun isFirstSignIn(uid: String): Boolean
    suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel?
}
