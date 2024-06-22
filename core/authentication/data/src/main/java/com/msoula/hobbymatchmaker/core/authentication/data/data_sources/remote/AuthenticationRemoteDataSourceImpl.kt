package com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote

import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.facebook.login.LoginManager
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.CreateUserError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.SignInError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.mappers.toUserDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.models.UserDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthenticationRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val credentialManager: CredentialManager
) : AuthenticationRemoteDataSource {

    private val facebookManager = LoginManager.getInstance()

    override fun authenticationSignOut() {
        auth.signOut()
        Log.d("HMM", "Logged out from Email/Pwd")
    }

    override suspend fun credentialManagerLogOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        Log.d("HMM", "Logged out from Google")
    }

    override fun loginManagerSignOut() {
        facebookManager.logOut()
        Log.d("HMM", "Logged out from Facebook")
    }

    override fun getCurrentAuthenticationUser(): Flow<UserDomainModel?> = flow {
        emit(auth.toUserDomainModel())
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Boolean> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.Success(true)
        } catch (e: Exception) {
            Log.e("HMM", "Exception caught while creating user: ${e.message}")
            if (e is FirebaseAuthException) {
                when (e.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> Result.Failure(CreateUserError.EmailAlreadyExists)
                    "ERROR_USER_DISABLED" -> Result.Failure(CreateUserError.UserDisabled)
                    "ERROR_TOO_MANY_REQUESTS" -> Result.Failure(CreateUserError.TooManyRequests)
                    "ERROR_INTERNAL_ERROR" -> Result.Failure(CreateUserError.InternalError)

                    else -> Result.Failure(
                        CreateUserError.Other(
                            message = e.message ?: "Could not create user"
                        )
                    )
                }
            } else {
                Result.Failure(CreateUserError.Other(message = "Unexpected error with error message: ${e.message}"))
            }
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(true)
        } catch (e: Exception) {
            Log.e("HMM", "Exception caught while logging user: ${e.message}")
            if (e is FirebaseAuthException) {
                when (e.errorCode) {
                    "ERROR_USER_DISABLED" -> Result.Failure(SignInError.UserDisabled)
                    "ERROR_USER_NOT_FOUND" -> Result.Failure(SignInError.UserNotFound)
                    "ERROR_WRONG_PASSWORD" -> Result.Failure(SignInError.WrongPassword)
                    "ERROR_TOO_MANY_REQUESTS" -> Result.Failure(SignInError.TooManyRequests)
                    else -> Result.Failure(SignInError.Other(""))
                }
            } else {
                Result.Failure(SignInError.Other("Unexpected error with error message: ${e.message}"))
            }
        }
    }

    override suspend fun resetPassword(email: String): Result<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Failure(
                ResetPasswordError(
                    message = e.message ?: "Could not reset password"
                )
            )
        }
    }

    override suspend fun signInWithCredentials(credential: AuthCredential): Result<Boolean> {
        return try {
            auth.signInWithCredential(credential).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Failure(
                SocialMediaError(
                    message = e.message ?: "Could not sign in with credentials: $credential"
                )
            )
        }
    }
}
