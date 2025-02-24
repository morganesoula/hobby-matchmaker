package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.GetAppleCredentialError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.GetGoogleCredentialError
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

interface IosAuthenticationRepository {
    suspend fun getGoogleCredentials(): Result<Pair<AuthCredential, String?>, GetGoogleCredentialError>
    suspend fun getAppleCredentials(): Result<Pair<AuthCredential, String?>, GetAppleCredentialError>
    suspend fun connectWithCredentials(credential: AuthCredential): Result<Pair<AuthCredential, String?>, GetAppleCredentialError>
}
