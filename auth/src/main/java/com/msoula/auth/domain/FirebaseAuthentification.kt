package com.msoula.auth.domain

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthentification {

    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    private fun createSignInIntent() = AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(providers)
        .build()

    fun launchSignInLauncher(signInLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent = createSignInIntent()

        Log.e("HMM", "SignInIntent is: ${signInIntent.data}")
        signInLauncher.launch(signInIntent)
    }

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse

        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            println("XXX - User is: ${user?.email}")
        } else {
            Log.e("HMM", "Error in logging firebase user with error: ${response?.error}")
        }
    }
}