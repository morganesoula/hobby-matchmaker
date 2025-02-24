package com.msoula.hobbymatchmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.credentials.CredentialManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.msoula.hobbymatchmaker.app.HobbyMatchMakerApp
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchmakerTheme
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AndroidGoogleUIClient
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    val auth: FirebaseAuth by inject()
    private lateinit var authStateListener: AuthStateListener

    private val googleClient by lazy {
        AndroidGoogleUIClient(
            credentialManager = CredentialManager.create(this),
            context = this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAuthenticationListener()

        setContent {
            HobbyMatchmakerTheme {
                HobbyMatchMakerApp(googleUIClient = googleClient)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun setAuthenticationListener() {
        authStateListener = AuthStateListener { _ ->
            //If you want to add some data to the session, you can do it here
        }
    }
}
