package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import android.content.Context
import com.facebook.AccessToken
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.GetFacebookClientError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.GetGoogleCredentialError
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

interface AndroidAuthenticationRepository {
    suspend fun getGoogleCredentials(context: Context): Result<Pair<AuthCredential, String?>, GetGoogleCredentialError>
    suspend fun fetchFacebookCredentials(token: String): Result<AuthCredential, GetGoogleCredentialError>
    suspend fun fetchFacebookClient(accessToken: AccessToken): Result<String?, GetFacebookClientError>
}
