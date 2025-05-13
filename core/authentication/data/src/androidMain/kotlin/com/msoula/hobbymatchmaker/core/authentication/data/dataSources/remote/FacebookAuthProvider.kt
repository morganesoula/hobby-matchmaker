package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.facebook.login.LoginManager
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors.ProviderError
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toFirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth

class FacebookAuthProvider(
    private val auth: FirebaseAuth
) : AuthProvider {

    override suspend fun signIn(credentials: AuthCredential): Result<FirebaseUserInfoDomainModel, ProviderError> {
        return try {
            val authResult = auth.signInWithCredential(credentials)

            authResult.user?.let {
                Result.Success(it.toFirebaseUserInfoDomainModel())
            } ?: run {
                Result.Failure(ProviderError.FacebookSignInError("Error while sign in with Facebook"))
            }
        } catch (e: Exception) {
            Result.Failure(ProviderError.FacebookSignInError("Error while sign in with Facebook + ${e.message}"))
        }
    }

    override suspend fun signOut(): Result<Boolean, ProviderError> {
        return try {
            LoginManager.getInstance().logOut()
            auth.signOut()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Failure(ProviderError.ProviderLogOutError("Error while sign out with Facebook + ${e.message}"))
        }
    }

    override fun isSignedIn(): Boolean = auth.currentUser != null
}
