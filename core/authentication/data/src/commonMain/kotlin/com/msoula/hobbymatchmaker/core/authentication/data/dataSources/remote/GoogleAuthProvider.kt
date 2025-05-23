package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors.ProviderError
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toFirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth

class GoogleAuthProvider(private val auth: FirebaseAuth) : AuthProvider {

    override suspend fun signIn(credentials: AuthCredential): Result<FirebaseUserInfoDomainModel, ProviderError> {
        return try {
            val authResult = auth.signInWithCredential(credentials)

            authResult.user?.let {
                Result.Success(it.toFirebaseUserInfoDomainModel())
            } ?: run {
                Result.Failure(ProviderError.GoogleSignInError("Error while sign in with Google"))
            }
        } catch (e: Exception) {
            Result.Failure(ProviderError.GoogleSignInError("Error while sign in with Google + ${e.message}"))
        }
    }

    override suspend fun signOut(): Result<Boolean, ProviderError> {
        return try {
            auth.signOut()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Failure(ProviderError.ProviderLogOutError("Error while logging out from Google + ${e.message}"))
        }
    }

    override fun isSignedIn(): Boolean = auth.currentUser != null
}
