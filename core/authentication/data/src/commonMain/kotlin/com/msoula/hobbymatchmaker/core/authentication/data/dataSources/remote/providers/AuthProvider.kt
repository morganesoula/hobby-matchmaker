package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.providers

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors.ProviderErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

interface AuthProvider {
    suspend fun signIn(credentials: AuthCredential): Result<FirebaseUserInfoDomainModel, ProviderErrorHMM>
    suspend fun signOut(): Result<Boolean, ProviderErrorHMM>
    fun isSignedIn(): Boolean
}
