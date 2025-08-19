package com.msoula.hobbymatchmaker.core.authentication.domain.repositories

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

interface AuthenticationRepository {
    suspend fun logOut(): Result<Boolean, LogOutErrorHMM>
    suspend fun signUp(
        email: String,
        password: String,
    ): Result<String, CreateUserWithEmailAndPasswordErrorHMM>

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<String, SignInWithEmailAndPasswordErrorHMM>

    suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordErrorHMM>
    suspend fun signInWithCredential(
        authCredential: AuthCredential,
        providerType: ProviderType
    ): Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM>

    suspend fun linkInWithCredential(
        authCredential: AuthCredential
    ): Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM>

    suspend fun isFirstSignIn(uid: String): Boolean
    suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel?
}
