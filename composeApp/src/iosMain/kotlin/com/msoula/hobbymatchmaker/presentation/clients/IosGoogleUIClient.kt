package com.msoula.hobbymatchmaker.presentation.clients

import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClient
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import nativeIosShared.IosNativeSocialCredential
import platform.UIKit.UIApplication
import kotlin.coroutines.resumeWithException

class IosGoogleUIClient : GoogleUIClient {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getGoogleCredentials(): AuthCredential? {
        return suspendCancellableCoroutine { continuation ->
            val viewController =
                UIApplication.sharedApplication.keyWindow?.rootViewController as? objcnames.classes.UIViewController
                    ?: run {
                        continuation.resumeWithException(IllegalStateException("no rootViewController found"))
                        return@suspendCancellableCoroutine
                    }

            IosNativeSocialCredential.shared()
                .getGoogleCredentialsFrom(viewController) { idToken, error ->
                    if (error != null) {
                        continuation.resumeWithException(Exception(error.localizedDescription))
                    } else idToken?.let {
                        val credential = GoogleAuthProvider.credential(idToken, null)
                        continuation.resume(credential) { cause, _, _ ->
                            continuation.resumeWithException(Exception(cause.message))
                        }
                    } ?: run {
                        continuation.resumeWithException(Exception("Unknown error occurred during Google Sign-In"))
                    }
                }
        }
    }
}

