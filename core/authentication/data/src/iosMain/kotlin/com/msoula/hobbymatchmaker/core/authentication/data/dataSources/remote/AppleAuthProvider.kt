package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors.ProviderError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential

class AppleAuthProvider(
    private val auth: FirebaseAuth
) : AuthProvider {

    override suspend fun signIn(credentials: AuthCredential): Result<FirebaseUserInfoDomainModel, ProviderError> {
        return try {
            val result = auth.signInWithCredential(credentials)
            val user = result.user

            if (user != null) {
                Result.Success(
                    FirebaseUserInfoDomainModel(
                        uid = user.uid,
                        email = user.email ?: "",
                        providers = user.providerData.map { it.providerId }
                    )
                )
            } else {
                Result.Failure(ProviderError.AppleSignInError("User is null"))
            }
        } catch (e: Exception) {
            Logger.e("❌ Apple sign-in exception: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
            Result.Failure(ProviderError.AppleSignInError("Firebase sign-in failed: ${e.message ?: "Unknown error"}"))
        }
    }

    override suspend fun signOut(): Result<Boolean, ProviderError> {
        return try {
            auth.signOut()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Failure(
                ProviderError.ProviderLogOutError(
                    "Error while sign out with Apple + ${e.message}"
                )
            )
        }
    }

    override fun isSignedIn(): Boolean = auth.currentUser != null
}

private fun mapUserToFirebaseUserInfoDomainModel(
    credential: ASAuthorizationAppleIDCredential
): FirebaseUserInfoDomainModel {
    return FirebaseUserInfoDomainModel(
        uid = credential.user,
        email = credential.email ?: "",
        providers = listOf("apple.com")
    )
}
