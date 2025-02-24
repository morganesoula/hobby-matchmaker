package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import android.content.Context
import com.facebook.AccessToken
import com.msoula.hobbymatchmaker.core.authentication.domain.dataSources.AndroidAuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.GetFacebookClientError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.GetGoogleCredentialError
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

class AndroidAuthenticationRepositoryImpl(
    private val remoteDataSource: AndroidAuthenticationRemoteDataSource
): AndroidAuthenticationRepository {

    override suspend fun fetchFacebookClient(accessToken: AccessToken): Result<String?, GetFacebookClientError> =
        remoteDataSource.fetchFacebookClient(accessToken)

    override suspend fun fetchFacebookCredentials(token: String): Result<AuthCredential, GetGoogleCredentialError> =
        remoteDataSource.fetchFacebookCredentials(token)

    override suspend fun getGoogleCredentials(context: Context): Result<Pair<AuthCredential, String?>, GetGoogleCredentialError> {
        return remoteDataSource.connectWithGoogle(context)
    }
}
