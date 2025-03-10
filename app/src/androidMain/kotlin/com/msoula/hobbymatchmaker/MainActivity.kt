package com.msoula.hobbymatchmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cafe.adriel.voyager.navigator.Navigator
import coil3.compose.setSingletonImageLoaderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchmakerTheme
import com.msoula.hobbymatchmaker.presentation.components.App
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val auth: FirebaseAuth by inject()
    private lateinit var authStateListener: AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAuthenticationListener()

        setContent {
            HobbyMatchmakerTheme {
                Navigator(App())
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
