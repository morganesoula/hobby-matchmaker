package com.msoula.hobbymatchmaker.core.authentication.domain.fakes

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordError
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

class FakeAuthenticationRepository : AuthenticationRepository {

    var signUpResult: Result<String, CreateUserWithEmailAndPasswordError> =
        Result.Success("fakeUid")
    var signInResult: Result<String, SignInWithEmailAndPasswordError> = Result.Success("fakeUid")
    var resetPasswordResult: Result<Boolean, ResetPasswordError> = Result.Success(true)
    var logOutResult: Result<Boolean, LogOutError> = Result.Success(true)
    var signInWithCredentialResult: Result<FirebaseUserInfoDomainModel, SocialMediaError> =
        Result.Success(
            FirebaseUserInfoDomainModel(
                uid = "fakeUid",
                email = "fake@example.com",
                providers = emptyList()
            )
        )
    var linkInWithCredentialResult: Result<FirebaseUserInfoDomainModel, SocialMediaError> =
        Result.Success(
            FirebaseUserInfoDomainModel(
                uid = "linkedUid",
                email = "linked@example.com",
                providers = emptyList()
            )
        )
    var isFirstSignInResult: Boolean = true
    var fetchUserInfoResult: FirebaseUserInfoDomainModel? =
        FirebaseUserInfoDomainModel(
            uid = "fakeUid",
            email = "fake@example.com",
            providers = emptyList()
        )

    override suspend fun logOut(): Result<Boolean, LogOutError> {
        return logOutResult
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): Result<String, CreateUserWithEmailAndPasswordError> {
        return signUpResult
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordError> {
        return signInResult
    }

    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError> {
        return resetPasswordResult
    }

    override suspend fun signInWithCredential(
        authCredential: AuthCredential,
        providerType: ProviderType
    ): Result<FirebaseUserInfoDomainModel, SocialMediaError> {
        return signInWithCredentialResult
    }

    override suspend fun linkInWithCredential(authCredential: AuthCredential): Result<FirebaseUserInfoDomainModel, SocialMediaError> {
        return linkInWithCredentialResult
    }

    override suspend fun isFirstSignIn(uid: String): Boolean {
        return isFirstSignInResult
    }

    override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel? {
        return fetchUserInfoResult
    }
}
