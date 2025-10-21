package com.msoula.hobbymatchmaker.core.authentication.data.repositories

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toFirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.AuthState
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import com.msoula.hobbymatchmaker.core.common.requireNonNull
import dev.gitlive.firebase.auth.AuthCredential

class AuthenticationRepositoryImpl(
    private val remoteDataSource: AuthenticationRemoteDataSource
) : AuthenticationRepository {
    override suspend fun logOut(): AppResult<Unit, AppError> =
        remoteDataSource.authenticationSignOut()

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

    override suspend fun signInWithCredential(
        authCredential: AuthCredential,
        providerType: ProviderType
    ): AppResult<FirebaseUserInfoDomainModel, AppError> =
        remoteDataSource.signInWithCredentials(authCredential, providerType)
            .requireNonNull { AppError.Authentication.Unknown }
            .mapSuccess {
                it.toFirebaseUserInfoDomainModel()
            }

    override suspend fun linkInWithCredential(
        authCredential: AuthCredential
    ): AppResult<FirebaseUserInfoDomainModel, AppError> =
        remoteDataSource.linkWithCredential(credential = authCredential)
            .requireNonNull { AppError.Authentication.Unknown }
            .mapSuccess {
                it.toFirebaseUserInfoDomainModel()
            }

    override suspend fun isFirstSignIn(uid: String): AppResult<Boolean, AppError> =
        remoteDataSource.isFirstSignIn(uid)

    override suspend fun fetchFirebaseUserInfo(): AppResult<AuthState, AppError> =
        remoteDataSource.fetchFirebaseUserInfo()
            .mapSuccess { userData ->
                userData?.toFirebaseUserInfoDomainModel()
                    ?.let { AuthState.Authenticated(it) }
                    ?: AuthState.SignedOut
            }
}
