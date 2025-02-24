package com.msoula.hobbymatchmaker.core.login.presentation.clients

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.GetFacebookClientError
import com.msoula.hobbymatchmaker.core.common.Result
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.FacebookAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AndroidFacebookUIClient(
    private val context: Context
) : FacebookUIClient {

    val callbackManager = CallbackManager.Factory.create()
    val loginManager = LoginManager.getInstance()

    override suspend fun getFacebookCredentials(): Pair<AuthCredential, String?> {
        return suspendCancellableCoroutine { continuation ->
            loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    continuation.resumeWithException(Exception("Facebook login cancelled"))
                }

                override fun onError(error: FacebookException) {
                    continuation.resumeWithException(Exception("Facebook login error: ${error.message}"))
                }

                override fun onSuccess(result: LoginResult) {
                    val accessToken = result.accessToken
                    val credential = FacebookAuthProvider.credential(accessToken.token)

                    CoroutineScope(Dispatchers.IO).launch {
                        when (val emailResult = fetchFacebookUserProfile(accessToken)) {
                            is Result.Success -> continuation.resume(credential to emailResult.data)
                            is Result.Failure -> continuation.resumeWithException(
                                Exception("Error fetching Facebook email: ${emailResult.error.message}")
                            )

                            else -> Unit
                        }
                    }
                }
            })

            loginManager.logInWithReadPermissions(
                context as Activity,
                listOf("email", "public_profile")
            )
        }
    }

    suspend fun fetchFacebookUserProfile(
        accessToken: AccessToken
    ): Result<String?, GetFacebookClientError> {
        return try {
            suspendCancellableCoroutine { continuation ->
                val request = GraphRequest.newMeRequest(accessToken) { jsonObject, _ ->
                    try {
                        val email = jsonObject?.getString("email")
                        if (email != null) {
                            continuation.resume(Result.Success(email))
                        } else {
                            continuation.resume(
                                Result.Failure(
                                    GetFacebookClientError("Error with parsing Facebook email object")
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("HMM", "Error fetching Facebook profile: ${e.message}")
                        continuation.resume(
                            Result.Failure(
                                GetFacebookClientError(
                                    e.message ?: "Error parsing Facebook profile"
                                )
                            )
                        )
                    }
                }

                val parameters = Bundle().apply { putString("fields", "email, name") }
                request.parameters = parameters
                request.executeAsync()

                continuation.invokeOnCancellation {
                    request.callback = null
                }
            }
        } catch (e: Exception) {
            Result.Failure(GetFacebookClientError(e.message ?: "Error fetching Facebook profile"))
        }
    }
}
