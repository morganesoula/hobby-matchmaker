package com.msoula.hobbymatchmaker.presentation.clients

import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClient
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import nativeIosShared.IosNativeSocialCredential
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
import kotlin.coroutines.resume

class IosGoogleUIClient : GoogleUIClient {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getGoogleCredentials(): AppResult<AuthCredential?, AppError> {
        return suspendCancellableCoroutine { continuation ->
            val viewController = getRootViewController()
                ?: run {
                    continuation.resume(AppResult.Failure(AppError.Authentication.Unknown))
                    return@suspendCancellableCoroutine
                }

                IosNativeSocialCredential.shared().getGoogleCredentialsFrom(viewController) { idToken, accessToken, error ->
                    if (error != null) {
                        continuation.resume(AppResult.Failure(AppError.Authentication.InvalidCredentials))
                    } else if (idToken != null && accessToken != null) {
                        val credential = GoogleAuthProvider.credential(idToken, accessToken)
                        continuation.resume(AppResult.Success(credential))
                    } else {
                        continuation.resume(AppResult.Failure(AppError.Authentication.Unknown))
                    }
                }
        }
    }

    fun getRootViewController(): UIViewController? = run {
        val scene = UIApplication.sharedApplication.connectedScenes.firstOrNull() as? UIWindowScene
            ?: return null

        for (window in scene.windows) {
            val uiWindow = window as UIWindow
            if (uiWindow.isKeyWindow()) {
                return@run uiWindow.rootViewController
            }
        }

        null
    }
}

