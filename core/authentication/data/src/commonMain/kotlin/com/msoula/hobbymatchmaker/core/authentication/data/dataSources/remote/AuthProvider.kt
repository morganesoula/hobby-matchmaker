package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors.ProviderError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

interface AuthProvider {
    suspend fun signIn(credentials: AuthCredential): Result<FirebaseUserInfoDomainModel, ProviderError>
    suspend fun signOut(): Result<Boolean, ProviderError>
    fun isSignedIn(): Boolean
}
