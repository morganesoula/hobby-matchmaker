package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.errors.ProviderError
import com.msoula.hobbymatchmaker.core.authentication.domain.models.FirebaseUserInfoDomainModel
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

class AppleAuthProvider(
    private val auth: FirebaseAuth
) : AuthProvider {

    override suspend fun signIn(credentials: AuthCredential): Result<FirebaseUserInfoDomainModel, ProviderError> {
        return suspendCancellableCoroutine { continuation ->
            val provider = ASAuthorizationAppleIDProvider()
            val request = provider.createRequest()
            request.requestedScopes =
                listOf(ASAuthorizationScopeFullName, ASAuthorizationScopeEmail)

            val controller = ASAuthorizationController(listOf(request))
            controller.delegate = object : NSObject(), ASAuthorizationControllerDelegateProtocol {
                override fun authorizationController(
                    controller: ASAuthorizationController,
                    didCompleteWithAuthorization: ASAuthorization
                ) {
                    val credential =
                        didCompleteWithAuthorization.credential as? ASAuthorizationAppleIDCredential

                    if (credential != null) {
                        continuation.resume(
                            Result.Success(
                                mapUserToFirebaseUserInfoDomainModel(credential)
                            )
                        )
                    } else {
                        continuation.resume(Result.Failure(ProviderError.AppleSignInError("Error while sign in with Apple")))
                    }
                }

                override fun authorizationController(
                    controller: ASAuthorizationController,
                    didCompleteWithError: NSError
                ) {
                    continuation.resume(
                        Result.Failure(
                            ProviderError.AppleSignInError(
                                "Error while sign in + ${didCompleteWithError.localizedDescription}"
                            )
                        )
                    )
                }
            }

            controller.performRequests()
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
