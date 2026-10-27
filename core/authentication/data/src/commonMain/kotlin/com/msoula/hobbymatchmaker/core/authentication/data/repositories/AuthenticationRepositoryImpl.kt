package com.msoula.hobbymatchmaker.core.authentication.data.repositories

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.local.AuthenticationLocalDataSource
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toAuthenticatedUser
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toProviderTypeDataModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthenticatedUser
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.flatMapSuspend
import com.msoula.hobbymatchmaker.core.common.onFailure
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.common.requireNonNull
import dev.gitlive.firebase.auth.AuthCredential

class AuthenticationRepositoryImpl(
    private val remoteDataSource: AuthenticationRemoteDataSource,
    private val localDataSource: AuthenticationLocalDataSource
) : AuthenticationRepository {
    override suspend fun logOut(): AppResult<Unit, AppError> =
        remoteDataSource.authenticationSignOut()

    override suspend fun deleteAccount(): AppResult<Unit, AppError> =
        remoteDataSource.deleteAccount()
            .flatMapSuspend {
                // The account is already gone remotely: a provider sign-out failure must not fail the deletion
                remoteDataSource.authenticationSignOut()
                    .onFailure { error -> Logger.e("Provider sign-out after account deletion failed: $error") }
                localDataSource.clearUserData()
            }

    override suspend fun signUp(
        email: String,
        password: String,
    ): AppResult<String, AppError> =
        remoteDataSource.createUserWithEmailAndPassword(email, password)

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): AppResult<String, AppError> =
        remoteDataSource.signInWithEmailAndPassword(email, password)

    override suspend fun resetPassword(email: String): AppResult<Unit, AppError> =
        remoteDataSource.resetPassword(email)

    override suspend fun linkInWithCredential(
        providerType: ProviderType,
        credentialProvider: suspend () -> Any?
    ): AppResult<AuthenticatedUser, AppError> {
        val credential = credentialProvider() as? AuthCredential
            ?: return AppResult.Failure(AppError.Authentication.InvalidCredentials)

        return remoteDataSource.linkWithCredential(credential)
            .requireNonNull { AppError.Authentication.Unknown }
            .mapSuccess { it.toAuthenticatedUser() }
    }


    override suspend fun isFirstSignIn(uid: String): AppResult<Boolean, AppError> =
        remoteDataSource.isFirstSignIn(uid)

    override suspend fun fetchUserInfo(): AppResult<AuthState, AppError> =
        remoteDataSource.fetchFirebaseUserInfo()
            .mapSuccess { userData ->
                userData?.toAuthenticatedUser()
                    ?.let { AuthState.Authenticated(it) }
                    ?: AuthState.SignedOut
            }

    override suspend fun signInWithSocialProvider(
        providerType: ProviderType,
        credentialProvider: suspend () -> Any?
    ): AppResult<AuthenticatedUser?, AppError> =
        remoteDataSource.signInWithSocialProvider(providerType.toProviderTypeDataModel(), credentialProvider)
            .mapSuccess { it?.toAuthenticatedUser() }
}
