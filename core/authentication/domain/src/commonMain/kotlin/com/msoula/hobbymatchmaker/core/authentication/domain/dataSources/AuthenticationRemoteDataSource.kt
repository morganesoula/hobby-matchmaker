package com.msoula.hobbymatchmaker.core.authentication.domain.dataSources

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

interface AuthenticationRemoteDataSource {
    suspend fun authenticationSignOut(): Result<Boolean, LogOutError>
    suspend fun signInWithCredentials(credential: AuthCredential, providerType: ProviderType)
        : Result<FirebaseUserInfoDomainModel, SocialMediaError>

    suspend fun linkWithCredential(credential: AuthCredential): Result<FirebaseUserInfoDomainModel, SocialMediaError>
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
