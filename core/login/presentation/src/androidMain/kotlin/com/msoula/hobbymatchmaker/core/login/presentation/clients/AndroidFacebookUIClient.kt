package com.msoula.hobbymatchmaker.core.login.presentation.clients

import android.app.Activity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FacebookAuthProvider

class AndroidFacebookUIClient(
    private val activityProvider: () -> Activity,
    private val callbackManager: CallbackManager?
) : FacebookUIClient {

    private val loginManager = LoginManager.getInstance()

    override fun registerCallback(
        onSuccess: (AuthCredential, String?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                onError(Exception("Facebook login cancelled"))
            }

            override fun onError(error: FacebookException) {
                onError(Exception("Facebook login error: ${error.message}"))
            }

            override fun onSuccess(result: LoginResult) {
                val accessToken = result.accessToken
                val credential: AuthCredential =
                    FacebookAuthProvider.credential(accessToken.token)

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

    override fun hasValidToken(): Boolean {
        val token = AccessToken.getCurrentAccessToken()
        return token != null && !token.isExpired
    }
}
