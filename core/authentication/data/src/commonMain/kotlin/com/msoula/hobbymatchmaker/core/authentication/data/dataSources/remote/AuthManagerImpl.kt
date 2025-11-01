package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.AuthProvider
import com.msoula.hobbymatchmaker.core.authentication.data.models.AuthFirebaseUser
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import dev.gitlive.firebase.auth.AuthCredential

class AuthManagerImpl(providers: List<AuthProvider>) : AuthManager {

    private val map = providers.associateBy { it.type }

    override suspend fun signIn(
        providerType: ProviderType,
        credential: AuthCredential
    ): AppResult<AuthFirebaseUser?, AppError> =
        map[providerType]?.signIn(credential)
            ?: AppResult.Failure(
                AppError.External.Service(
                    providerType.id, "provider_not_found"
                )
            )

    override suspend fun signOut(): AppResult<Unit, AppError> {
        var firstError: AppError? = null
        for (provider in map.values) {
            when (val res = provider.signOut()) {
                is AppResult.Success -> Unit
                is AppResult.Failure -> if (firstError == null) firstError = res.error
            }
        }
        return firstError?.let { AppResult.Failure(it) } ?: AppResult.Success(Unit)
    }
}
