package com.msoula.hobbymatchmaker.core.login.presentation.clients

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.msoula.hobbymatchmaker.core.common.AppError
import com.msoula.hobbymatchmaker.core.common.AppResult
import com.msoula.hobbymatchmaker.core.common.DefaultDispatcherProvider
import com.msoula.hobbymatchmaker.core.common.Logger
import com.msoula.hobbymatchmaker.core.login.presentation.BuildKonfig.WEB_CLIENT_ID
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.withContext

class AndroidGoogleUIClient(
    private val credentialManager: CredentialManager,
    private val context: Context,
) : GoogleUIClient {

    private val dispatcherProvider = DefaultDispatcherProvider()

    private val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(WEB_CLIENT_ID)
        .setAutoSelectEnabled(true)
        .build()

    private val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun getGoogleCredentials(): AppResult<AuthCredential?, AppError> {
        return withContext(dispatcherProvider.io) {
            try {
                val response = launchGetCredential()
                response?.let {
                    when (val result = handleSignIn(it)) {
                        is AppResult.Success -> AppResult.Success(result.data.first)
                        is AppResult.Failure -> AppResult.Failure(result.error)
                    }
                } ?: AppResult.Failure(AppError.Authentication.InvalidCredentials)
            } catch (e: Exception) {
                Log.e("HMM", "Error getting credential: $e")
                AppResult.Failure(AppError.Authentication.InvalidCredentials)
            }
        }
    }

    private suspend fun launchGetCredential(): GetCredentialResponse? {
        return try {
            credentialManager.getCredential(
                request = request,
                context = context
            )
        } catch (e: GetCredentialException) {
            Log.e("HMM", "Error getting credential: $e")
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun handleSignIn(result: GetCredentialResponse): AppResult<Pair<AuthCredential, String?>, AppError> {
        val credential = result.credential

        return if (credential is CustomCredential && credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val email = googleIdTokenCredential.id

            val authCredential =
                GoogleAuthProvider.credential(googleIdTokenCredential.idToken, null)

            AppResult.Success(Pair(authCredential, email))
        } else {
            Logger.e("Unexpected credential type: ${credential::class.simpleName}")
            AppResult.Failure(AppError.Authentication.InvalidCredentials)
        }
    }
}

