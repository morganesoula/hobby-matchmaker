package com.msoula.hobbymatchmaker.core.login.presentation.clients

import android.app.Activity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.msoula.hobbymatchmaker.core.common.Logger
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FacebookAuthProvider

class AndroidFacebookUIClient(
    private val activityProvider: () -> Activity,
    private val callbackManager: CallbackManager?
) : FacebookUIClient {

    private val loginManager = LoginManager.getInstance()

    override suspend fun getFacebookCredentials(): Pair<AuthCredential?, String?> {
        return Pair(null, "")
    }

    override fun registerCallback(
        onSuccess: (AuthCredential, String?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                Logger.d("Facebook login cancelled")
                onError(Exception("Facebook login cancelled"))
            }

            override fun onError(error: FacebookException) {
                Logger.d("Facebook login error: ${error.message}")
                onError(Exception("Facebook login error: ${error.message}"))
            }

            override fun onSuccess(result: LoginResult) {
                val accessToken = result.accessToken
                val credential = FacebookAuthProvider.credential(accessToken.token)
                onSuccess(credential, null)
            }
        })
    }

    override fun logIn() {
        loginManager.logInWithReadPermissions(
            activityProvider(),
            listOf("email", "public_profile")
        )
    }
}
