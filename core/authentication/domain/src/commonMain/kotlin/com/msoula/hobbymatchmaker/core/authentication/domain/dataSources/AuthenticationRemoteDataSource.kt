package com.msoula.hobbymatchmaker.core.authentication.domain.dataSources

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

interface AuthenticationRemoteDataSource {
    suspend fun authenticationSignOut(): Result<Boolean, LogOutErrorHMM>
    suspend fun signInWithCredentials(credential: AuthCredential, providerType: ProviderType)
        : Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM>

    suspend fun linkWithCredential(credential: AuthCredential): Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM>
    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, CreateUserWithEmailAndPasswordErrorHMM>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordErrorHMM>

    suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordErrorHMM>
    suspend fun getUserUid(): String?
    suspend fun isFirstSignIn(uid: String): Boolean
    suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel?
}
