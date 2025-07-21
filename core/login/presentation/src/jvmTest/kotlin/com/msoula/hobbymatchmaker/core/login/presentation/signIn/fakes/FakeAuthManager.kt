package com.msoula.hobbymatchmaker.core.login.presentation.signIn.fakes

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthManager
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors.ProviderError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

class FakeAuthManager(private val shouldFail: Boolean = false) : AuthManager {
    private val fakeUser = FirebaseUserInfoDomainModel(
        uid = "fakeUID123",
        email = "test@test.fr",
        providers = emptyList()
    )

    override suspend fun signIn(
        providerType: ProviderType,
        credential: AuthCredential
    ): Result<FirebaseUserInfoDomainModel, ProviderError> {
        return if (credential is FakeAuthCredential) {
            if (shouldFail) {
                Result.Failure(
                    ProviderError.GoogleSignInError(
                        "Simulated sign-in error"
                    )
                )
            } else {
                Result.Success(fakeUser)
            }
        } else {
            Result.Failure(ProviderError.GoogleSignInError("Invalid credential type"))
        }
    }

    override suspend fun signOut(): Result<Boolean, ProviderError> = Result.Success(true)
}
