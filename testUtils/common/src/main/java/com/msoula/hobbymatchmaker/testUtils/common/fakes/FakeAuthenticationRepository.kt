package com.msoula.hobbymatchmaker.testUtils.common.fakes

import android.content.Context
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.GetFacebookClientError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.GetGoogleCredentialError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.Result

class FakeAuthenticationRepository : AuthenticationRepository {

    private var shouldLogOut: Boolean = true

    override suspend fun logOut(): Result<Boolean, LogOutError> {
        return if (shouldLogOut) {
            Result.Success(true)
        } else {
            Result.Failure(LogOutError.UnknownError("Could not log out"))
        }
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): Result<String, CreateUserWithEmailAndPasswordError> {
        return if (email.isEmpty() && password.isEmpty()) {
            Result.Failure(CreateUserWithEmailAndPasswordError.InternalError)
        } else if (email.isNotEmpty() && password.isEmpty()) {
            Result.Failure(CreateUserWithEmailAndPasswordError.EmailAlreadyExists)
        } else {
            Result.Success("")
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordError> {
        return if (email.isNotEmpty() && password.isNotEmpty()) {
            Result.Success("")
        } else if (password.isEmpty() && email.isNotEmpty()) {
            Result.Failure(SignInWithEmailAndPasswordError.WrongPassword)
        } else {
            Result.Failure(SignInWithEmailAndPasswordError.Other("Empty email value"))
        }
    }

    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError> {
        return if (email.isNotEmpty()) {
            Result.Success(true)
        } else {
            Result.Failure(ResetPasswordError(""))
        }
    }

    override suspend fun signInWithCredential(authCredential: AuthCredential): Result<AuthResult?, SocialMediaError> {
        TODO("Not yet implemented")
    }

    override suspend fun linkInWithCredential(authCredential: AuthCredential): Result<AuthResult?, SocialMediaError> {
        TODO("Not yet implemented")
    }

    override suspend fun isFirstSignIn(uid: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel {
        return FirebaseUserInfoDomainModel("uid123", "", emptyList())
    }

    override suspend fun connectWithGoogle(context: Context): Result<Pair<AuthCredential, String?>, GetGoogleCredentialError> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchFacebookCredentials(token: String): Result<AuthCredential, GetGoogleCredentialError> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchFacebookClient(accessToken: AccessToken): Result<String?, GetFacebookClientError> {
        TODO("Not yet implemented")
    }

    fun shouldLogOut(logOut: Boolean) {
        shouldLogOut = logOut
    }
}
