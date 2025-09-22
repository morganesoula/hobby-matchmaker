package com.msoula.hobbymatchmaker.tests.fakes

import dev.gitlive.firebase.auth.AuthCredential
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthenticationRemoteDataSource
import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthFirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import kotlinx.coroutines.delay

class FakeAuthenticationRemoteDataSource : AuthenticationRemoteDataSource {
    var createUserResult: AppResult<String, AppError> = AppResult.Success("uid-123")
    var signInResult: AppResult<String, AppError> = AppResult.Success("uid-123")
    var resetResult: AppResult<Unit, AppError> = AppResult.Success(Unit)
    var socialResult: AppResult<AuthFirebaseUser?, AppError> =
        AppResult.Success(AuthFirebaseUser(uid = "uid-123", email = "user@mail.com", providers = listOf("google.com")))
    var socialDelayMs: Long = 0L
    var signInDelayMs: Long = 0L
    var resetDelayMs: Long = 0L
    var signUpDelayMs: Long = 0L

    override suspend fun authenticationSignOut(): AppResult<Unit, AppError> =
        error("not used")

    override suspend fun signInWithCredentials(
        credential: AuthCredential,
        providerType: ProviderType
    ): AppResult<AuthFirebaseUser?, AppError> {
        if (socialDelayMs > 0) delay(socialDelayMs)
        return socialResult
    }

    override suspend fun linkWithCredential(credential: AuthCredential): AppResult<AuthFirebaseUser?, AppError> =
        error("not used")

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): AppResult<String, AppError> {
        if (signUpDelayMs > 0) delay(signUpDelayMs)
        return createUserResult
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): AppResult<String, AppError> {
        if (signInDelayMs > 0) delay(signInDelayMs)
        return signInResult
    }

    override suspend fun resetPassword(email: String): AppResult<Unit, AppError> {
        if (resetDelayMs > 0) delay(resetDelayMs)
        return resetResult
    }

    override fun getUserUid(): String? = error("not used")

    override suspend fun isFirstSignIn(uid: String): AppResult<Boolean, AppError> =
        error("not used")

    override suspend fun fetchFirebaseUserInfo(): AppResult<AuthFirebaseUser?, AppError> =
        error("not used")
}
