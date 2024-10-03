package com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote

import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.facebook.login.LoginManager
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.CreateUserError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.LinkWithCredentialsError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.SignInError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.data.data_sources.remote.mappers.toFirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.data_sources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.safeCall
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class AuthenticationRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val credentialManager: CredentialManager,
    private val firestore: FirebaseFirestore
) : AuthenticationRemoteDataSource {

    private val facebookManager = LoginManager.getInstance()

    override suspend fun authenticationSignOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            facebookManager.logOut()
            auth.signOut()
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("HMM", "Exception caught while logging out: ${e.message}")
        }
        Log.d("HMM", "Logged out")
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.Success(auth.currentUser?.uid ?: "")
        } catch (exception: CancellationException) {
            throw exception
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
    ): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw FirebaseAuthException(
                "ERROR_USER_NOT_FOUND",
                "User not found"
            )
            Result.Success(uid)
        } catch (exception: CancellationException) {
            throw exception
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
        return safeCall(
            appError = { errorMessage -> ResetPasswordError("Could not reset password: $errorMessage") }
        ) {
            auth.sendPasswordResetEmail(email).await()
            true
        }
    }

    override suspend fun signInWithCredentials(
        credential: AuthCredential
    ): Result<AuthResult> {
        return safeCall(
            appError = { errorMessage -> SocialMediaError("Could not sign in with credentials: $credential - $errorMessage") }
        ) {
            auth.signInWithCredential(credential).await()
        }
    }

    override suspend fun linkWithCredential(credential: AuthCredential): Result<AuthResult?> {
        return safeCall(
            appError = { errorMessage ->
                LinkWithCredentialsError(
                    "Exception while linking with credentials - $errorMessage"
                )
            }
        ) {
            auth.currentUser?.linkWithCredential(credential)?.await()
        }
    }

    override suspend fun getUserUid(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun isFirstSignIn(uid: String): Boolean {
        if (uid.isEmpty()) {
            return true
        }

        val userDocument = firestore.collection("users").document(uid).get().await()
        // Return true if user does NOT exist
        return !userDocument.exists()
    }

    override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel? {
        return auth.currentUser?.toFirebaseUserInfoDomainModel()
    }
}
