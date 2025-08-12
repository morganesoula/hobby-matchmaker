package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.providers

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors.ProviderError
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toFirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth

class AnonymousAuthProvider(
    private val auth: FirebaseAuth
) : AuthProvider {
    override suspend fun signIn(credentials: AuthCredential): Result<FirebaseUserInfoDomainModel, ProviderError> {
        return Result.Loading
    }

    override suspend fun signOut(): Result<Boolean, ProviderError> {
        return Result.Loading
    }

    override fun isSignedIn(): Boolean = auth.currentUser != null

    override suspend fun signInAnonymously(): Result<FirebaseUserInfoDomainModel, ProviderError> =
        try {
            val result = auth.signInAnonymously()
            val user = result.user
            user?.let {
                Result.Success(user.toFirebaseUserInfoDomainModel())
            } ?: Result.Failure(
                ProviderError.AnonymousSignInError(
                    "No user after anonymous sign-in"
                )
            )
        } catch (e: Exception) {
            Result.Failure(ProviderError.AnonymousSignInError("Anonymous failed - ${e.message}"))
        }
}
