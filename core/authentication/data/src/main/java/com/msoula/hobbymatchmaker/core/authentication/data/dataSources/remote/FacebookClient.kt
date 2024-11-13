package com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote

import android.os.Bundle
import android.util.Log
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.msoula.hobbymatchmaker.core.authentication.domain.errors.GetFacebookClientError
import com.msoula.hobbymatchmaker.core.common.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class FacebookClient(
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun fetchFacebookUserProfile(
        accessToken: AccessToken
    ): Result<String?, GetFacebookClientError> {
        return try {
            withContext(dispatcher) {
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
                                    GetFacebookClientError(e.message ?: "Error parsing Facebook profile")
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
            }
        } catch (e: Exception) {
            Result.Failure(GetFacebookClientError(e.message ?: "Error fetching Facebook profile"))
        }
    }

}
