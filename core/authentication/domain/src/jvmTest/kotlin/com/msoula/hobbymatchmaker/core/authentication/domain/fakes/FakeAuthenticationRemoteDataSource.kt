package com.msoula.hobbymatchmaker.core.authentication.domain.fakes

import com.msoula.hobbymatchmaker.core.authentication.domain.dataSources.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.CreateUserWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.LogOutErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.ResetPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SignInWithEmailAndPasswordErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.SocialMediaErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import dev.gitlive.firebase.auth.AuthCredential

class FakeAuthenticationRemoteDataSource: AuthenticationRemoteDataSource {

    var authenticationSignOutResult: Result<Boolean, LogOutErrorHMM> =
        Result.Success(true)

    var createUserResult: (email: String, password: String) -> Result<String, CreateUserWithEmailAndPasswordErrorHMM> =
        { email, password ->
            when {
                email.isEmpty() && password.isEmpty() -> Result.Failure(CreateUserWithEmailAndPasswordErrorHMM.UserDisabled)
                password.isEmpty() -> Result.Failure(CreateUserWithEmailAndPasswordErrorHMM.InternalErrorHMM)
                email.isEmpty() -> Result.Failure(CreateUserWithEmailAndPasswordErrorHMM.TooManyRequests)
                email == password -> Result.Failure(CreateUserWithEmailAndPasswordErrorHMM.EmailAlreadyExists)
                email == "unknown error" -> Result.Failure(CreateUserWithEmailAndPasswordErrorHMM.Other("Weird error message"))
                else -> Result.Success("fakeUid")
            }
        }

    var signInWithEmailAndPasswordResult: (email: String, password: String) -> Result<String, SignInWithEmailAndPasswordErrorHMM> =
        { email, password ->
            when {
                email.isEmpty() && password.isEmpty() -> Result.Failure(SignInWithEmailAndPasswordErrorHMM.UserDisabled)
                password.isEmpty() -> Result.Failure(SignInWithEmailAndPasswordErrorHMM.WrongPassword)
                email.isEmpty() -> Result.Failure(SignInWithEmailAndPasswordErrorHMM.UserNotFound)
                email == "unknown error" -> Result.Failure(SignInWithEmailAndPasswordErrorHMM.Other("Weird error message"))
                else -> Result.Success("fakeUUID")
            }
        }

    override suspend fun authenticationSignOut(): Result<Boolean, LogOutErrorHMM> {
        return authenticationSignOutResult
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, CreateUserWithEmailAndPasswordErrorHMM> {
        return createUserResult(email, password)
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<String, SignInWithEmailAndPasswordErrorHMM> {
        return signInWithEmailAndPasswordResult(email, password)
    }

    override suspend fun signInWithCredentials(
        credential: AuthCredential,
        providerType: ProviderType
    ): Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> = TODO()

    override suspend fun linkWithCredential(
        credential: AuthCredential
    ): Result<FirebaseUserInfoDomainModel, SocialMediaErrorHMM> = TODO()

    override suspend fun resetPassword(email: String): Result<Boolean, ResetPasswordErrorHMM> = TODO()

    override suspend fun getUserUid(): String? = TODO()

    override suspend fun isFirstSignIn(uid: String): Boolean = TODO()

    override suspend fun fetchFirebaseUserInfo(): FirebaseUserInfoDomainModel? = TODO()
}
