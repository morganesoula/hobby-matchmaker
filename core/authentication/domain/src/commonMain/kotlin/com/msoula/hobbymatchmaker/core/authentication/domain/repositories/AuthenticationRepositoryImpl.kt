package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import com.msoula.hobbymatchmaker.core.authentication.domain.dataSources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import dev.gitlive.firebase.auth.AuthCredential
import kotlinx.coroutines.CancellationException

class AuthenticationRepositoryImpl(
    private val remoteDataSource: AuthenticationRemoteDataSource
) : AuthenticationRepository {
    override suspend fun logOut(): Result<Boolean, LogOutErrorHMM> {
        return try {
            when (val result = remoteDataSource.authenticationSignOut()) {
                is Result.Success, Result.Loading -> {
                    Result.Success(true)
                }

                is Result.Failure -> {
                    Result.Failure(result.error)
                }
            }
        } catch (exception: Exception) {
            Result.Failure(LogOutErrorHMM.UnknownErrorHMM(exception.message ?: ""))
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
    ): Result<String, CreateUserWithEmailAndPasswordErrorHMM> {
        return try {
            remoteDataSource.createUserWithEmailAndPassword(email, password)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(
                CreateUserWithEmailAndPasswordErrorHMM.Other(
                    msg = e.message ?: "Error while creating user"
                )
            )
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<String, SignInWithEmailAndPasswordErrorHMM> {
        return remoteDataSource.signInWithEmailAndPassword(email, password)
            .mapSuccess { it }
            .mapError { error ->
                return@mapError error
            }
    }

    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordErrorHMM> =
        remoteDataSource.resetPassword(email)

    override suspend fun signInWithCredential(
        authCredential: AuthCredential,
        providerType: ProviderType
    ): Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> {
        return remoteDataSource.signInWithCredentials(authCredential, providerType)
    }

    override suspend fun linkInWithCredential(
        authCredential: AuthCredential
    ): Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> {
        return remoteDataSource.linkWithCredential(credential = authCredential)
    }

    override suspend fun isFirstSignIn(uid: String): Boolean {
        return remoteDataSource.isFirstSignIn(uid)
    }

    override suspend fun fetchFirebaseUserInfo() =
        remoteDataSource.fetchFirebaseUserInfo()
}
