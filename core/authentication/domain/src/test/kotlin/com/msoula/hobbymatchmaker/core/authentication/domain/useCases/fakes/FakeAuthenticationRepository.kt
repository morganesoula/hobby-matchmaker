package com.msoula.hobbymatchmaker.core.authentication.domain.useCases.fakes

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
        TODO("Not yet implemented")
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordError> {
        TODO("Not yet implemented")
    }

    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError> {
        TODO("Not yet implemented")
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

    override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel? {
        TODO("Not yet implemented")
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
