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

class FakeAuthenticationRepository(
    private val fakeSessionRepository: FakeSessionRepository
) : AuthenticationRepository {

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
        return if (fakeSessionRepository.isConnectedFlow.value) {
            Result.Success(true)
        } else {
            Result.Failure(LogOutError.UnknownError("weird error message"))
        }
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): Result<String, CreateUserWithEmailAndPasswordError> {
        return when {
            email.isEmpty() && password.isEmpty() -> Result.Failure(
                CreateUserWithEmailAndPasswordError.UserDisabled
            )

            password.isEmpty() -> Result.Failure(CreateUserWithEmailAndPasswordError.InternalError)
            email.isEmpty() -> Result.Failure(CreateUserWithEmailAndPasswordError.TooManyRequests)
            email == password -> Result.Failure(CreateUserWithEmailAndPasswordError.EmailAlreadyExists)
            email == "unknown error" -> Result.Failure(CreateUserWithEmailAndPasswordError.Other("Weird error message"))
            else -> Result.Success("fakeUid")
        }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordError> {
        return when {
            email.isEmpty() && password.isEmpty() -> Result.Failure(SignInWithEmailAndPasswordError.UserDisabled)
            password.isEmpty() -> Result.Failure(SignInWithEmailAndPasswordError.WrongPassword)
            email.isEmpty() -> Result.Failure(SignInWithEmailAndPasswordError.UserNotFound)
            email == "unknown error" -> Result.Failure(SignInWithEmailAndPasswordError.Other("Weird error message"))
            else -> Result.Success("fakeUUID")
        }
    }

    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError> {
        return when {
            email.isEmpty() -> Result.Failure(ResetPasswordError.Other)
            email == "too many requests" -> Result.Failure(ResetPasswordError.TooManyRequests)
            email == "connection issue" -> Result.Failure(ResetPasswordError.Connection)
            else -> Result.Success(true)
        }
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
