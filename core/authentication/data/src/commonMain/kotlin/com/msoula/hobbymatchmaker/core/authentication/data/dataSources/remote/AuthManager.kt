package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors.ProviderError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential

class AuthManager(private val providers: List<AuthProvider>) {

    suspend fun signIn(
        providerType: ProviderType,
        credential: AuthCredential
    ): Result<FirebaseUserInfoDomainModel, ProviderError> {
        val provider = providers.find { it::class.simpleName == providerType.className }
            ?: return Result.Failure(ProviderError.NoProviderFound("Provider not found"))

        return provider.signIn(credential)
    }

    suspend fun signOut(): Result<Boolean, ProviderError> {
        var hasError = false
        Logger.d("Signing out in AuthManager")
        providers.forEach { provider ->
            val result = provider.signOut()
            if (result is Result.Failure) hasError = true
        }

        return if (hasError) Result.Failure(
            ProviderError.ProviderLogOutError(
                "Some providers failed to log out"
            )
        ) else {
            Result.Success(true)
        }
    }
}
