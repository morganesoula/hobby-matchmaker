package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthenticatedUser
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult

interface AuthenticationRepository {
    suspend fun logOut(): AppResult<Unit, AppError>
    suspend fun signUp(
        email: String,
        password: String,
    ): AppResult<String, AppError>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): AppResult<String, AppError>

    suspend fun resetPassword(email: String): AppResult<Unit, AppError>

    suspend fun signInWithSocialProvider(
        providerType: ProviderType,
        credentialProvider: suspend () -> Any?
    ): AppResult<AuthenticatedUser?, AppError>

    suspend fun linkInWithCredential(
        providerType: ProviderType,
        credentialProvider: suspend () -> Any?
    ): AppResult<AuthenticatedUser, AppError>

    suspend fun isFirstSignIn(uid: String): AppResult<Boolean, AppError>
    suspend fun fetchUserInfo(): AppResult<AuthState, AppError>
}
