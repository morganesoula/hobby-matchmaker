package com.msoula.hobbymatchmaker.core.login.presentation.sign_in

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.msoula.hobbymatchmaker.core.login.presentation.BuildConfig

class GoogleAuthClient(
    private val credentialManager: CredentialManager,
    private val context: Context
) {

    private val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.WEB_CLIENT_ID)
        .setAutoSelectEnabled(true)
        .setNonce("random-string")
        .build()

    private val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    suspend fun launchGetCredential(): GetCredentialResponse? {
        return try {
            credentialManager.getCredential(
                request = request,
                context = context
            )
        } catch (e: GetCredentialException) {
            e.printStackTrace()
            null
        }
    }

    fun handleSignIn(result: GetCredentialResponse): Pair<AuthCredential, String?> {
        val credential = result.credential
        return if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val email = googleIdTokenCredential.id

            val authCredential =
                GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

            Pair(authCredential, email)
        } else {
            throw RuntimeException("Received an invalid credential type")
        }
    }
}
