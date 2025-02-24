package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

interface AuthenticationRepository {
    suspend fun logOut(): Result<Boolean, LogOutError>
    suspend fun signUp(
        email: String,
        password: String,
    ): Result<String, CreateUserWithEmailAndPasswordError>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<String, SignInWithEmailAndPasswordError>

    suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError>
    suspend fun signInWithCredential(
        authCredential: AuthCredential,
        providerType: ProviderType
    ): Result<FirebaseUserInfoDomainModel, SocialMediaError>

    suspend fun linkInWithCredential(
        authCredential: AuthCredential
    ): Result<FirebaseUserInfoDomainModel, SocialMediaError>

    suspend fun isFirstSignIn(uid: String): Boolean
    suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel?
}
