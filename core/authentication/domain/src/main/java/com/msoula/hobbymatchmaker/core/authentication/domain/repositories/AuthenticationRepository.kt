package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.UserDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import kotlinx.coroutines.flow.Flow

class AuthenticationRepository(
    private val remoteDataSource: AuthenticationRemoteDataSource
) {

    fun observeAuthState(): Flow<UserDomainModel?> =
        remoteDataSource.getCurrentAuthenticationUser()

    suspend fun logOut(): Result<Boolean> {
        return try {
            remoteDataSource.oneTapClientSignOut()
            remoteDataSource.loginManagerSignOut()
            remoteDataSource.authenticationSignOut()
            Result.Success(true)
        } catch (exception: Exception) {
            Result.Failure(LogOutError(message = exception.message ?: "Error while logging out"))
        }
    }

    suspend fun signUp(
        email: String,
        password: String,
    ): Result<Boolean> {
        return remoteDataSource.createUserWithEmailAndPassword(email, password)
            .mapSuccess { it }
            .mapError { error ->
                return@mapError error
            }
    }


    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<Boolean> {
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
}
