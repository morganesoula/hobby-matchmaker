package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.providers

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors.ProviderErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toFirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth

class GoogleAuthProvider(private val auth: FirebaseAuth) : AuthProvider {

    override suspend fun signIn(credentials: AuthCredential): Result<FirebaseUserInfoDomainModel, ProviderErrorHMM> {
        return try {
            val authResult = auth.signInWithCredential(credentials)

            authResult.user?.let {
                Result.Success(it.toFirebaseUserInfoDomainModel())
            } ?: run {
                Result.Failure(ProviderErrorHMM.GoogleSignInErrorHMM("Error while sign in with Google"))
            }
        } catch (e: Exception) {
            Result.Failure(ProviderErrorHMM.GoogleSignInErrorHMM("Error while sign in with Google + ${e.message}"))
        }
    }

    override suspend fun signOut(): Result<Boolean, ProviderErrorHMM> {
        return try {
            auth.signOut()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Failure(ProviderErrorHMM.ProviderLogOutErrorHMM("Error while logging out from Google + ${e.message}"))
        }
    }

    override fun isSignedIn(): Boolean = auth.currentUser != null
}
