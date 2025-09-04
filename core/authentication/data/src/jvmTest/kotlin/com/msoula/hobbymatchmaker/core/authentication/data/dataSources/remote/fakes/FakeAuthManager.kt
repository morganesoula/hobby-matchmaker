package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.fakes

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthManager
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors.ProviderErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import dev.gitlive.firebase.auth.AuthCredential

class FakeAuthManager: AuthManager {

    var signOutResult: Result<Boolean, ProviderErrorHMM> = Result.Success(true)
    var shouldThrow = false

    override suspend fun signOut(): Result<Boolean, ProviderErrorHMM> {
        if (shouldThrow) throw Exception("Some unexpected exception")
        return signOutResult
    }

    override suspend fun signIn(
        providerType: ProviderType,
        credential: AuthCredential
    ): Result<FirebaseUserInfoDomainModel, ProviderErrorHMM> {
        return Result.Success(
            FirebaseUserInfoDomainModel("uid", "email@fake.com", listOf("google.com"))
        )
    }
}
