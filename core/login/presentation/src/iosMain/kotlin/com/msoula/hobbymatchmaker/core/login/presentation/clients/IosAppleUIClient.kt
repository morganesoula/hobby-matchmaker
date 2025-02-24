package com.msoula.hobbymatchmaker.core.login.presentation.clients

import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.OAuthProvider
import kotlinx.cinterop.BetaInteropApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class IosAppleUIClient : AppleUIClient {

    override suspend fun getAppleCredentials(): AuthCredential? {
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
                        val identityToken = credential.identityToken?.toKotlinString()
                        if (identityToken != null) {
                            val authCredential =
                                OAuthProvider.credential("apple.com", identityToken, null)
                            continuation.resume(authCredential)
                        } else {
                            continuation.resumeWithException(Exception("Identity token is null"))
                        }
                    } else {
                        continuation.resumeWithException(Exception("Credential are null in IosAppleUIClient"))
                    }
                }

                override fun authorizationController(
                    controller: ASAuthorizationController,
                    didCompleteWithError: NSError
                ) {
                    continuation.resumeWithException(
                        Exception(
                            "Error from authorizationController is:" +
                                didCompleteWithError.localizedDescription
                        )
                    )
                }
            }

            controller.performRequests()
        }
    }
}

@OptIn(BetaInteropApi::class)
fun NSData.toKotlinString(): String? {
    return NSString.create(data = this, encoding = NSUTF8StringEncoding)?.toString()
}
