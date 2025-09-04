package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthFirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import dev.gitlive.firebase.auth.AuthCredential

interface AuthenticationRemoteDataSource {
    suspend fun authenticationSignOut(): AppResult<Unit, AppError>
    suspend fun signInWithCredentials(credential: AuthCredential, providerType: ProviderType)
        : AppResult<AuthFirebaseUser?, AppError>

    suspend fun linkWithCredential(credential: AuthCredential): AppResult<AuthFirebaseUser?, AppError>
    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): AppResult<String, AppError>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AppResult<String, AppError>

    suspend fun resetPassword(email: String): AppResult<Unit, AppError>
    fun getUserUid(): String?
    suspend fun isFirstSignIn(uid: String): AppResult<Boolean, AppError>
    suspend fun fetchFirebaseUserInfo(): AppResult<AuthFirebaseUser?, AppError>
}
