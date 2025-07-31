package com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes

import com.msoula.hobbymatchmaker.core.authentication.domain.dataSources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

class FakeAuthenticationRemoteDataSource(
    private val authenticationSignOutResult: Result<Boolean, LogOutError> =
        Result.Success(true),
    private val signInWithCredentialResult: Result<FirebaseUserInfoDomainModel, SocialMediaError> =
        Result.Success(
            FirebaseUserInfoDomainModel(
                "test-uid", "test-email", emptyList()
            )
        ),
    private val linkWithCredentialResult: Result<FirebaseUserInfoDomainModel, SocialMediaError> =
        Result.Success(
            FirebaseUserInfoDomainModel(
                "test-uid", "test-email", emptyList()
            )
        ),
    private val createUserWithEmailAndPasswordResult:
    Result<String, CreateUserWithEmailAndPasswordError> = Result.Success("test-uid"),
    private val signInWithEmailAndPasswordResult: Result<String, SignInWithEmailAndPasswordError> =
        Result.Success("test-uid"),
    private val resetPasswordResult: Result<Boolean, ResetPasswordError> =
        Result.Success(true),
    private val isFirstSignValue: Boolean = true
) : AuthenticationRemoteDataSource {
    override suspend fun authenticationSignOut(): Result<Boolean, LogOutError> =
        authenticationSignOutResult

    override suspend fun signInWithCredentials(
        credential: AuthCredential,
        providerType: ProviderType
    ): Result<FirebaseUserInfoDomainModel, SocialMediaError> = signInWithCredentialResult

    override suspend fun linkWithCredential(credential: AuthCredential):
        Result<FirebaseUserInfoDomainModel, SocialMediaError> = linkWithCredentialResult

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, CreateUserWithEmailAndPasswordError> = createUserWithEmailAndPasswordResult

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordError> = signInWithEmailAndPasswordResult

    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError> =
        resetPasswordResult

    override suspend fun getUserUid(): String? = "test-uid"

    override suspend fun isFirstSignIn(uid: String): Boolean = isFirstSignValue

    override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel? =
        FirebaseUserInfoDomainModel("test-uid", "test-email", emptyList())
}
