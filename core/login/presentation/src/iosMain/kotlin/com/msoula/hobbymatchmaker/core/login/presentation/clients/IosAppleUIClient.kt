package com.msoula.hobbymatchmaker.core.login.presentation.clients

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.Logger
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.OAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import nativeIosAuthShared.AppleSignInManager
import kotlin.coroutines.resume

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
class IosAppleUIClient : AppleUIClient {

    override suspend fun getAppleCredentials(): AppResult<AuthCredential?, AppError> =
        suspendCancellableCoroutine { continuation ->
            AppleSignInManager.shared().signInWithApple { token, nonce, error ->
                if (!continuation.isActive) return@signInWithApple

                when {
                    token != null && nonce != null -> {
                        val credential = OAuthProvider.credential(
                            providerId = "apple.com",
                            idToken = token,
                            rawNonce = nonce
                        )
                        continuation.resume(AppResult.Success(credential))
                    }

                    error != null -> {
                        Logger.e("Error in getAppleCredentials is: $error")
                        continuation.resume(AppResult.Failure(AppError.Authentication.Unknown))
                    }

                    else -> continuation.resume(AppResult.Failure(AppError.Authentication.Unknown))
                }
            }
        }
}
