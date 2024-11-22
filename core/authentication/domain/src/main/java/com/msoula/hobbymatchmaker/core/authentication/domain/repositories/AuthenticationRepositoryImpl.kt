package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import android.content.Context
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.msoula.hobbymatchmaker.core.authentication.domain.dataSources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import kotlinx.coroutines.CancellationException

class AuthenticationRepositoryImpl(
    private val remoteDataSource: AuthenticationRemoteDataSource
) : AuthenticationRepository {
    override suspend fun logOut(): Result<Boolean, LogOutError> {
        return try {
            when (val result = remoteDataSource.authenticationSignOut()) {
                is Result.Success, Result.Loading -> Result.Success(true)
                is Result.Failure -> {
                    Result.Failure(result.error)
                }
            }
        } catch (exception: Exception) {
            Result.Failure(LogOutError.UnknownError(exception.message ?: ""))
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
    ): Result<String, CreateUserWithEmailAndPasswordError> {
        return try {
            remoteDataSource.createUserWithEmailAndPassword(email, password)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Failure(
                CreateUserWithEmailAndPasswordError.Other(
                    message = e.message ?: "Error while creating user"
                )
            )
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<String, SignInWithEmailAndPasswordError> {
        return remoteDataSource.signInWithEmailAndPassword(email, password)
            .mapSuccess { it }
            .mapError { error ->
                return@mapError error
            }
    }

    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError> =
        remoteDataSource.resetPassword(email)

    override suspend fun signInWithCredential(
        authCredential: AuthCredential
    ): Result<AuthResult?, SocialMediaError> {
        return remoteDataSource.signInWithCredentials(authCredential)
    }

    override suspend fun linkInWithCredential(
        authCredential: AuthCredential
    ): Result<AuthResult?, SocialMediaError> {
        return remoteDataSource.linkWithCredential(credential = authCredential)
    }

    override suspend fun isFirstSignIn(uid: String): Boolean {
        return remoteDataSource.isFirstSignIn(uid)
    }

    override suspend fun fetchFirebaseUserInfo() =
        remoteDataSource.fetchFirebaseUserInfo()

    override suspend fun connectWithGoogle(context: Context) =
        remoteDataSource.connectWithGoogle(context)

    override suspend fun fetchFacebookCredentials(token: String) =
        remoteDataSource.fetchFacebookCredentials(token)

    override suspend fun fetchFacebookClient(accessToken: AccessToken) =
        remoteDataSource.fetchFacebookClient(accessToken)
}
