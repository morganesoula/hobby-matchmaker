package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import dev.gitlive.firebase.auth.AuthCredential

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
    suspend fun signInWithCredential(
        authCredential: AuthCredential,
        providerType: ProviderType
    ): AppResult<FirebaseUserInfoDomainModel, AppError>

    suspend fun linkInWithCredential(
        authCredential: AuthCredential
    ): AppResult<FirebaseUserInfoDomainModel, AppError>

    suspend fun isFirstSignIn(uid: String): AppResult<Boolean, AppError>
    suspend fun fetchFirebaseUserInfo(): AppResult<AuthState, AppError>
}
