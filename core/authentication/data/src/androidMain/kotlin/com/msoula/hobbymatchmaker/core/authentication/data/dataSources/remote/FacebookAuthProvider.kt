package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.facebook.login.LoginManager
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors.ProviderErrorHMM
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.mappers.toFirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.providers.AuthProvider
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth

class FacebookAuthProvider(
    private val auth: FirebaseAuth
) : AuthProvider {

    override suspend fun signIn(credentials: AuthCredential): Result<FirebaseUserInfoDomainModel, ProviderErrorHMM> {
        return try {
            val authResult = auth.signInWithCredential(credentials)

            authResult.user?.let {
                Result.Success(it.toFirebaseUserInfoDomainModel())
            } ?: run {
                Result.Failure(ProviderErrorHMM.FacebookSignInErrorHMM("Error while sign in with Facebook"))
            }
        } catch (e: Exception) {
            Result.Failure(ProviderErrorHMM.FacebookSignInErrorHMM("Error while sign in with Facebook + ${e.message}"))
        }
    }

    override suspend fun signOut(): Result<Boolean, ProviderErrorHMM> {
        return try {
            LoginManager.getInstance().logOut()
            auth.signOut()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Failure(ProviderErrorHMM.ProviderLogOutErrorHMM("Error while sign out with Facebook + ${e.message}"))
        }
    }

    override fun isSignedIn(): Boolean = auth.currentUser != null
}
