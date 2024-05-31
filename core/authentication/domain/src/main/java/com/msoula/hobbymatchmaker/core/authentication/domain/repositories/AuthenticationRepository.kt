package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationLocalDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ConnexionMode
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.mapError
import com.msoula.hobbymatchmaker.core.common.mapSuccess
import kotlinx.coroutines.flow.Flow

class AuthenticationRepository(
    private val remoteDataSource: AuthenticationRemoteDataSource,
    private val localDataSource: AuthenticationLocalDataSource
) {

    fun observeAuthenticationState(): Flow<Boolean> =
        localDataSource.observeAuthenticationState()

    suspend fun logOut(connexionMode: ConnexionMode): Result<Boolean> {
        return try {
            when (connexionMode) {
                ConnexionMode.FACEBOOK -> remoteDataSource.loginManagerSignOut()
                ConnexionMode.EMAIL -> remoteDataSource.authenticationSignOut()
                ConnexionMode.GOOGLE -> remoteDataSource.oneTapClientSignOut()
            }
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

    suspend fun signInWithCredential(
        facebookCredential: AuthCredential? = null,
        googleToken: String? = null
    ): Result<Boolean> {
        val credentialToUse =
            facebookCredential ?: GoogleAuthProvider.getCredential(googleToken, null)

        return remoteDataSource.signInWithCredentials(credentialToUse)
    }
}
