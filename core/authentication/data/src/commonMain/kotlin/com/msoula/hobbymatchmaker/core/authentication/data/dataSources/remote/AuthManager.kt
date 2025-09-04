package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthFirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import dev.gitlive.firebase.auth.AuthCredential

interface AuthManager {
    suspend fun signIn(
        providerType: ProviderType,
        credential: AuthCredential
    ): AppResult<AuthFirebaseUser?, AppError>
    suspend fun signOut(): AppResult<Unit, AppError>
}
