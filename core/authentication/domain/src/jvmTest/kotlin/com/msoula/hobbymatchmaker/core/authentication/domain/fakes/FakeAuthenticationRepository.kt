package com.msoula.hobbymatchmaker.core.authentication.domain.fakes

import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.authentication.domain.repositories.AuthenticationRepository
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

class FakeAuthenticationRepository(
    private val fakeSessionRepository: FakeSessionRepository
) : AuthenticationRepository {

    var signInWithCredentialResult: Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> =
        Result.Success(
            FirebaseUserInfoDomainModel(
                uid = "fakeUid",
                email = "fake@example.com",
                providers = emptyList()
            )
        )
    var linkInWithCredentialResult: Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> =
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

    override suspend fun logOut(): Result<Boolean, LogOutErrorHMM> {
        return if (fakeSessionRepository.isConnectedFlow.value) {
            Result.Success(true)
        } else {
            Result.Failure(LogOutErrorHMM.UnknownErrorHMM("weird error message"))
        }
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): Result<String, CreateUserWithEmailAndPasswordErrorHMM> {
        return when {
            email.isEmpty() && password.isEmpty() -> Result.Failure(
                CreateUserWithEmailAndPasswordErrorHMM.UserDisabled
            )

            password.isEmpty() -> Result.Failure(CreateUserWithEmailAndPasswordErrorHMM.InternalErrorHMM)
            email.isEmpty() -> Result.Failure(CreateUserWithEmailAndPasswordErrorHMM.TooManyRequests)
            email == password -> Result.Failure(CreateUserWithEmailAndPasswordErrorHMM.EmailAlreadyExists)
            email == "unknown error" -> Result.Failure(CreateUserWithEmailAndPasswordErrorHMM.Other("Weird error message"))
            else -> Result.Success("fakeUid")
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordErrorHMM> {
        return when {
            email.isEmpty() && password.isEmpty() -> Result.Failure(SignInWithEmailAndPasswordErrorHMM.UserDisabled)
            password.isEmpty() -> Result.Failure(SignInWithEmailAndPasswordErrorHMM.WrongPassword)
            email.isEmpty() -> Result.Failure(SignInWithEmailAndPasswordErrorHMM.UserNotFound)
            email == "unknown error" -> Result.Failure(SignInWithEmailAndPasswordErrorHMM.Other("Weird error message"))
            else -> Result.Success("fakeUUID")
        }
    }

    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordErrorHMM> {
        return when {
            email.isEmpty() -> Result.Failure(ResetPasswordErrorHMM.Other)
            email == "too many requests" -> Result.Failure(ResetPasswordErrorHMM.TooManyRequests)
            email == "connection issue" -> Result.Failure(ResetPasswordErrorHMM.Connection)
            else -> Result.Success(true)
        }
    }

    override suspend fun signInWithCredential(
        authCredential: AuthCredential,
        providerType: ProviderType
    ): Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> {
        return signInWithCredentialResult
    }

    override suspend fun linkInWithCredential(authCredential: AuthCredential): Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> {
        return linkInWithCredentialResult
    }

    override suspend fun isFirstSignIn(uid: String): Boolean {
        return isFirstSignInResult
    }

    override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel? {
        return fetchUserInfoResult
    }
}
