package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthUserRemoteDataModel
import com.msoula.hobbymatchmaker.core.authentication.data.models.ProviderTypeDataModel
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import dev.gitlive.firebase.auth.AuthCredential

interface AuthenticationRemoteDataSource {
    suspend fun authenticationSignOut(): AppResult<Unit, AppError>
    suspend fun linkWithCredential(credential: AuthCredential): AppResult<AuthUserRemoteDataModel?, AppError>
    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): AppResult<String, AppError>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AppResult<String, AppError>

    suspend fun resetPassword(email: String): AppResult<Unit, AppError>
    suspend fun isFirstSignIn(uid: String): AppResult<Boolean, AppError>
    suspend fun fetchFirebaseUserInfo(): AppResult<AuthUserRemoteDataModel?, AppError>

    suspend fun signInWithSocialProvider(
        providerType: ProviderTypeDataModel,
        credentialProvider: suspend () -> Any?
    ): AppResult<AuthUserRemoteDataModel?, AppError>
}
