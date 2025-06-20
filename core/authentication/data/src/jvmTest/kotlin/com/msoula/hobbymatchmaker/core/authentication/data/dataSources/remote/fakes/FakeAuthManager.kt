package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.fakes

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthManager
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors.ProviderError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

class FakeAuthManager: AuthManager {

    var signOutResult: Result<Boolean, ProviderError> = Result.Success(true)
    var shouldThrow = false

    override suspend fun signOut(): Result<Boolean, ProviderError> {
        if (shouldThrow) throw Exception("Some unexpected exception")
        return signOutResult
    }

    override suspend fun signIn(
        providerType: ProviderType,
        credential: AuthCredential
    ): Result<FirebaseUserInfoDomainModel, ProviderError> {
        return Result.Success(
            FirebaseUserInfoDomainModel("uid", "email@fake.com", listOf("google.com"))
        )
    }
}
