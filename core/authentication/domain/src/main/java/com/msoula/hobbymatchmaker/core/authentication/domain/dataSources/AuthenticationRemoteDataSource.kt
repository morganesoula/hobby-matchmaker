package com.msoula.hobbymatchmaker.core.authentication.domain.dataSources

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.common.Result

interface AuthenticationRemoteDataSource {
    suspend fun authenticationSignOut()
    suspend fun signInWithCredentials(credential: AuthCredential): Result<AuthResult, SocialMediaError>
    suspend fun linkWithCredential(credential: AuthCredential): Result<AuthResult?, SocialMediaError>
    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, CreateUserWithEmailAndPasswordError>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordError>

    suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError>
    suspend fun getUserUid(): String?
    suspend fun isFirstSignIn(uid: String): Boolean
    suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel?
}
