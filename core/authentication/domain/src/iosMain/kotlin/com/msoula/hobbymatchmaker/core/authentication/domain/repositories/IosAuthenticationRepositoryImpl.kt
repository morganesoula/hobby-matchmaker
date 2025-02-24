package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import com.msoula.hobbymatchmaker.core.authentication.domain.dataSources.IosAuthenticationRemoteDataSource
import dev.gitlive.firebase.auth.AuthCredential

class IosAuthenticationRepositoryImpl(
    private val remoteDataSource: IosAuthenticationRemoteDataSource
) : IosAuthenticationRepository {
    override suspend fun connectWithCredentials(credential: AuthCredential) =
        remoteDataSource.connectWithCredentials(credential)

    override suspend fun getAppleCredentials() =
        remoteDataSource.getAppleCredentials()

    override suspend fun getGoogleCredentials() =
        remoteDataSource.getGoogleCredentials()
}
