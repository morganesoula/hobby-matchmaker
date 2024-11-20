package com.msoula.hobbymatchmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.msoula.hobbymatchmaker.app.HobbyMatchMakerApp
import com.msoula.hobbymatchmaker.core.authentication.data.dataSources.remote.GoogleClient
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchmakerTheme
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {

    val auth: FirebaseAuth by inject()
    private lateinit var authStateListener: AuthStateListener

    val googleClient by inject<GoogleClient> { parametersOf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAuthenticationListener()

        setContent {
            HobbyMatchmakerTheme {
                HobbyMatchMakerApp()
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
