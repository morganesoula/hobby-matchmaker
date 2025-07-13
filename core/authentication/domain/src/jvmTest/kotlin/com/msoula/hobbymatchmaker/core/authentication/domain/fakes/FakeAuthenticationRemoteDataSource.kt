package com.msoula.hobbymatchmaker.core.authentication.domain.fakes

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

class FakeAuthenticationRemoteDataSource: AuthenticationRemoteDataSource {

    var authenticationSignOutResult: Result<Boolean, LogOutError> =
        Result.Success(true)

    var createUserResult: (email: String, password: String) -> Result<String, CreateUserWithEmailAndPasswordError> =
        { email, password ->
            when {
                email.isEmpty() && password.isEmpty() -> Result.Failure(CreateUserWithEmailAndPasswordError.UserDisabled)
                password.isEmpty() -> Result.Failure(CreateUserWithEmailAndPasswordError.InternalError)
                email.isEmpty() -> Result.Failure(CreateUserWithEmailAndPasswordError.TooManyRequests)
                email == password -> Result.Failure(CreateUserWithEmailAndPasswordError.EmailAlreadyExists)
                email == "unknown error" -> Result.Failure(CreateUserWithEmailAndPasswordError.Other("Weird error message"))
                else -> Result.Success("fakeUid")
            }
        }

    var signInWithEmailAndPasswordResult: (email: String, password: String) -> Result<String, SignInWithEmailAndPasswordError> =
        { email, password ->
            when {
                email.isEmpty() && password.isEmpty() -> Result.Failure(SignInWithEmailAndPasswordError.UserDisabled)
                password.isEmpty() -> Result.Failure(SignInWithEmailAndPasswordError.WrongPassword)
                email.isEmpty() -> Result.Failure(SignInWithEmailAndPasswordError.UserNotFound)
                email == "unknown error" -> Result.Failure(SignInWithEmailAndPasswordError.Other("Weird error message"))
                else -> Result.Success("fakeUUID")
            }
        }

    override suspend fun authenticationSignOut(): Result<Boolean, LogOutError> {
        return authenticationSignOutResult
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, CreateUserWithEmailAndPasswordError> {
        return createUserResult(email, password)
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordError> {
        return signInWithEmailAndPasswordResult(email, password)
    }

    override suspend fun signInWithCredentials(
        credential: AuthCredential,
        providerType: ProviderType
    ): Result<FirebaseUserInfoDomainModel, SocialMediaError> = TODO()

    override suspend fun linkWithCredential(
        credential: AuthCredential
    ): Result<FirebaseUserInfoDomainModel, SocialMediaError> = TODO()

    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordError> = TODO()

    override suspend fun getUserUid(): String? = TODO()

    override suspend fun isFirstSignIn(uid: String): Boolean = TODO()

    override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel? = TODO()
}
