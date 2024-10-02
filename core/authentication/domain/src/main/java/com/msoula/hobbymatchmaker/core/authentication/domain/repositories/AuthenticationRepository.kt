package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess

class AuthenticationRepository(
    private val remoteDataSource: AuthenticationRemoteDataSource
) {
    suspend fun logOut(): Result<Boolean> {
        return try {
            remoteDataSource.authenticationSignOut()
            Result.Success(true)
        } catch (exception: Exception) {
            Result.Failure(LogOutError(message = exception.message ?: "Error while logging out"))
        }
    }

    suspend fun signUp(
        email: String,
        password: String,
    ): Result<String> {
        return remoteDataSource.createUserWithEmailAndPassword(email, password)
            .mapSuccess { it }
            .mapError { error ->
                return@mapError error
            }
    }

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<String> {
        return remoteDataSource.signInWithEmailAndPassword(email, password)
            .mapSuccess { it }
            .mapError { error ->
                return@mapError error
            }
    }

    suspend fun resetPassword(email: String): Result<Boolean> =
        remoteDataSource.resetPassword(email)
            .mapSuccess { it }
            .mapError { error ->
                return@mapError error
            }

    suspend fun signInWithCredential(
        authCredential: AuthCredential
    ): Result<AuthResult?> {
        return remoteDataSource.signInWithCredentials(authCredential)
    }

    suspend fun linkInWithCredential(
        authCredential: AuthCredential
    ): Result<AuthResult?> {
        return remoteDataSource.linkWithCredential(credential = authCredential)
    }

    suspend fun isFirstSignIn(uid: String): Boolean {
        return remoteDataSource.isFirstSignIn(uid)
    }

    suspend fun fetchFirebaseUserInfo() =
        remoteDataSource.fetchFirebaseUserInfo()
}
