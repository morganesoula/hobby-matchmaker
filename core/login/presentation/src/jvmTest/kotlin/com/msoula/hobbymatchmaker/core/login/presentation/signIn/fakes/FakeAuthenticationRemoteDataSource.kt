package com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import dev.gitlive.firebase.auth.AuthCredential

class FakeAuthenticationRemoteDataSource(
    private val authenticationSignOutResult: Result<Boolean, LogOutErrorHMM> =
        Result.Success(true),
    private val signInWithCredentialResult: Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> =
        Result.Success(
            FirebaseUserInfoDomainModel(
                "test-uid", "test-email", emptyList()
            )
        ),
    private val linkWithCredentialResult: Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> =
        Result.Success(
            FirebaseUserInfoDomainModel(
                "test-uid", "test-email", emptyList()
            )
        ),
    private val createUserWithEmailAndPasswordResult:
    Result<String, CreateUserWithEmailAndPasswordErrorHMM> = Result.Success("test-uid"),
    private val signInWithEmailAndPasswordResult: Result<String, SignInWithEmailAndPasswordErrorHMM> =
        Result.Success("test-uid"),
    private val resetPasswordResult: Result<Boolean, ResetPasswordErrorHMM> =
        Result.Success(true),
    private val isFirstSignValue: Boolean = true
) : AuthenticationRemoteDataSource {
    override suspend fun authenticationSignOut(): Result<Boolean, LogOutErrorHMM> =
        authenticationSignOutResult

    override suspend fun signInWithCredentials(
        credential: AuthCredential,
        providerType: ProviderType
    ): Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> = signInWithCredentialResult

    override suspend fun linkWithCredential(credential: AuthCredential):
        Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> = linkWithCredentialResult

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, CreateUserWithEmailAndPasswordErrorHMM> = createUserWithEmailAndPasswordResult

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordErrorHMM> = signInWithEmailAndPasswordResult

    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordErrorHMM> =
        resetPasswordResult

    override suspend fun getUserUid(): String = "test-uid"

    override suspend fun isFirstSignIn(uid: String): Boolean = isFirstSignValue

    override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel =
        FirebaseUserInfoDomainModel("test-uid", "test-email", emptyList())
}
