package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toFirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.dataSources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.Result
import com.msoula.hobbymatchmaker.core.common.safeCall
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseAuthException
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.cancellation.CancellationException

class AuthenticationRemoteDataSourceImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authManager: AuthManager
) : AuthenticationRemoteDataSource {

    override suspend fun authenticationSignOut(): Result<Boolean, LogOutError> {
        return try {
            authManager.signOut()
            Result.Success(true)
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: FirebaseAuthException) {
            Result.Failure(LogOutError.FirebaseException(e.message ?: ""))
        } catch (e: Exception) {
            //Log.e("HMM", "Exception caught while logging out: ${e.message}")
            println("Exception caught while logging out: ${e.message}")
            Result.Failure(LogOutError.UnknownError(e.message ?: ""))
        }
    }

    override suspend fun signInWithCredentials(
        credential: AuthCredential,
        providerType: ProviderType
    ): Result<FirebaseUserInfoDomainModel, SocialMediaError> {
        return try {
            when (val result = authManager.signIn(providerType, credential)) {
                is Result.Success -> Result.Success(result.data)
                is Result.Failure -> Result.Failure(SocialMediaError.SignInWithCredentialsError)
                else -> Result.Failure(SocialMediaError.SignInWithCredentialsError)
            }
        } catch (e: Exception) {
            Result.Failure(SocialMediaError.SignInWithCredentialsError)
        }
    }

    override suspend fun linkWithCredential(credential: AuthCredential): Result<FirebaseUserInfoDomainModel, SocialMediaError> {
        return try {
            val result = auth.currentUser?.linkWithCredential(credential)

            return if (result?.user != null) {
                Result.Success(result.user!!.toFirebaseUserInfoDomainModel())
            } else {
                Result.Failure(SocialMediaError.LinkWithCredentialsError)
            }
        } catch (e: Exception) {
            println("Exception caught while linking credentials: ${e.message}")
            Result.Failure(SocialMediaError.LinkWithCredentialsError)
        }
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, CreateUserWithEmailAndPasswordError> {
        return try {
            auth.createUserWithEmailAndPassword(email, password)
            Result.Success(auth.currentUser?.uid ?: "")
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: FirebaseAuthException) {
            //TODO No more e.errorCode so fix it
            when (e.message) {
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
        } catch (e: Exception) {
            //Log.e("HMM", "Exception caught while creating user: ${e.message}")
            println("Exception caught while creating user: ${e.message}")
            Result.Failure(
                CreateUserWithEmailAndPasswordError.Other(
                    e.message ?: "Unexpected error"
                )
            )
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordError> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password)
            val uid = result.user?.uid ?: throw Error("User not found")
            Result.Success(uid)
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: Exception) {
            //Log.e("HMM", "Exception caught while logging user: ${e.message}")
            if (e is FirebaseAuthException) {
                //TODO No more e.errorCode so fix it
                when (e.message) {
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
            appError = { errorMessage ->
                ResetPasswordError(
                    "Could not reset password: $errorMessage"
                )
            }
        ) {
            auth.sendPasswordResetEmail(email)
            true
        }
    }

    override suspend fun getUserUid(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun isFirstSignIn(uid: String): Boolean {
        if (uid.isEmpty()) {
            return true
        }

        val userDocument = firestore.collection("users").document(uid).get()
        // Return true if user does NOT exist
        return !userDocument.exists
    }

    override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel? {
        return auth.currentUser?.toFirebaseUserInfoDomainModel()
    }
}
