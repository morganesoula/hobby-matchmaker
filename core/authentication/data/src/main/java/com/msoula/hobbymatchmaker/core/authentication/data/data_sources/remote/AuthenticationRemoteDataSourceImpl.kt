package com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote

import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.facebook.login.LoginManager
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.CreateUserError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.FacebookError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.SignInError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.mappers.toUserDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.models.UserDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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
        return suspendCancellableCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        Result.Failure(
                            CreateUserError(
                                message = task.exception?.message ?: "Could not create user"
                            )
                        )
                    }
                }
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        Result.Failure(
                            SignInError(
                                message = task.exception?.message
                                    ?: "Could not sign in"
                            )
                        )
                    }
                }
        }
    }

    override suspend fun resetPassword(email: String): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        continuation.resume(
                            Result.Failure(
                                ResetPasswordError(
                                    message = task.exception?.message
                                        ?: "Could not reset password"
                                )
                            )
                        )
                    }
                }
        }
    }

    override suspend fun signInWithCredentials(credential: AuthCredential): Result<Boolean> {
        return suspendCancellableCoroutine { continuation ->
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Result.Success(true))
                    } else {
                        continuation.resume(
                            Result.Failure(
                                FacebookError(
                                    message = task.exception?.message
                                        ?: "Could not sign in with Facebook"
                                )
                            )
                        )
                    }
                }
        }
    }
}
