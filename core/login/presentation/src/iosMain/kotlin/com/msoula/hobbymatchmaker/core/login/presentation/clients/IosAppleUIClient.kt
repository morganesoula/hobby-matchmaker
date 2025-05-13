package com.msoula.hobbymatchmaker.core.login.presentation.clients

import com.msoula.hobbymatchmaker.core.common.Logger
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.OAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import nativeIosAuthShared.AppleSignInManager
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
class IosAppleUIClient : AppleUIClient {

    override suspend fun getAppleCredentials(): AuthCredential? {
        return suspendCancellableCoroutine { continuation ->
            AppleSignInManager.shared().signInWithApple { token, nonce, error ->
                when {
                    token != null && nonce != null -> {
                        val credential = OAuthProvider.credential(
                            providerId = "apple.com",
                            idToken = token,
                            rawNonce = nonce
                        )
                        continuation.resume(credential)
                    }

                    error != null -> {
                        Logger.e("Error in getAppleCredentials is: $error")
                        continuation.resumeWithException(Exception(error))
                    }

                    else -> continuation.resumeWithException(Exception("Unknown Apple Sign-In failure"))
                }
            }
        }
    }
}
