package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toFirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.dataSources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.GetFacebookClientError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.GetFacebookCredentialError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.GetGoogleCredentialError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.safeCall
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class AuthenticationRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val credentialManager: CredentialManager,
    private val firestore: FirebaseFirestore,
    private val googleClient: GoogleClient,
    private val facebookAuthClient: FacebookClient
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
            Log.e("HMM", "Exception caught while logging out: ${e.message}")
        }
        Log.d("HMM", "Logged out")
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, CreateUserWithEmailAndPasswordError> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.Success(auth.currentUser?.uid ?: "")
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: Exception) {
            Log.e("HMM", "Exception caught while creating user: ${e.message}")
            if (e is FirebaseAuthException) {
                when (e.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> Result.Failure(
                        CreateUserWithEmailAndPasswordError.EmailAlreadyExists
                    )

                    "ERROR_USER_DISABLED" -> Result.Failure(CreateUserWithEmailAndPasswordError.UserDisabled)
                    "ERROR_TOO_MANY_REQUESTS" -> Result.Failure(CreateUserWithEmailAndPasswordError.TooManyRequests)
                    "ERROR_INTERNAL_ERROR" -> Result.Failure(CreateUserWithEmailAndPasswordError.InternalError)

                    else -> Result.Failure(
                        CreateUserWithEmailAndPasswordError.Other(
                            message = e.message ?: "Could not create user"
                        )
                    )
                }
            } else {
                Result.Failure(
                    CreateUserWithEmailAndPasswordError.Other(
                        message = "Unexpected error with error message: ${e.message}"
                    )
                )
            }
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordError> {
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
                    "ERROR_USER_DISABLED" -> Result.Failure(SignInWithEmailAndPasswordError.UserDisabled)
                    "ERROR_USER_NOT_FOUND" -> Result.Failure(SignInWithEmailAndPasswordError.UserNotFound)
                    "ERROR_WRONG_PASSWORD" -> Result.Failure(SignInWithEmailAndPasswordError.WrongPassword)
                    "ERROR_TOO_MANY_REQUESTS" -> Result.Failure(SignInWithEmailAndPasswordError.TooManyRequests)
                    else -> Result.Failure(SignInWithEmailAndPasswordError.Other(""))
                }
            } else {
                Result.Failure(
                    SignInWithEmailAndPasswordError.Other(
                        "Unexpected error with error message: ${e.message}"
                    )
                )
            }
        }
    }

    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError> {
        return safeCall(
            appError = { errorMessage -> ResetPasswordError("Could not reset password: $errorMessage") }
        ) {
            auth.sendPasswordResetEmail(email).await()
            true
        }
    }

    override suspend fun signInWithCredentials(
        credential: AuthCredential
    ): Result<AuthResult, SocialMediaError> {
        return safeCall(
            appError = { SocialMediaError.SignInWithCredentialsError }
        ) {
            auth.signInWithCredential(credential).await()
        }
    }

    override suspend fun linkWithCredential(credential: AuthCredential): Result<AuthResult?, SocialMediaError> {
        return safeCall(
            appError = {
                SocialMediaError.LinkWithCredentialsError
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

    override suspend fun connectWithGoogle(context: Context): Result<Pair<AuthCredential, String?>, GetGoogleCredentialError> {
        return safeCall(
            appError = { errorMessage -> GetGoogleCredentialError(errorMessage) }
        ) {
            val result = googleClient.launchGetCredential(context)
            result?.let { googleClient.handleSignIn(it) }
                ?: throw Exception("Google credential is null")
        }
    }

    override suspend fun fetchFacebookCredentials(token: String): Result<AuthCredential, GetGoogleCredentialError> {
        return safeCall(
            appError = { errorMessage -> GetFacebookCredentialError(errorMessage) }
        ) {
            FacebookAuthProvider.getCredential(token)
        }
    }

    override suspend fun fetchFacebookClient(accessToken: AccessToken): Result<String?, GetFacebookClientError> {
        return try {
            facebookAuthClient.fetchFacebookUserProfile(accessToken)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.Failure(GetFacebookClientError(exception.message ?: ""))
        }
    }
}
