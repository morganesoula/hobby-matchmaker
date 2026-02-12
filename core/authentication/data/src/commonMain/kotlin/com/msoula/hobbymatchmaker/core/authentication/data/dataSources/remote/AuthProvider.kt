package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthUserRemoteDataModel
import com.msoula.hobbymatchmaker.core.authentication.data.models.ProviderTypeDataModel
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import dev.gitlive.firebase.auth.AuthCredential

interface AuthProvider {
    val type: ProviderTypeDataModel
    suspend fun signIn(credentials: AuthCredential): AppResult<AuthUserRemoteDataModel?, AppError>
    suspend fun signOut(): AppResult<Unit, AppError>
    fun isSignedIn(): Boolean
}
