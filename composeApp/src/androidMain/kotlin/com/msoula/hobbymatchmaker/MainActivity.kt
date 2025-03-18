package com.msoula.hobbymatchmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.credentials.CredentialManager
import cafe.adriel.voyager.navigator.Navigator
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchmakerTheme
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AndroidGoogleUIClient
import com.msoula.hobbymatchmaker.presentation.AppViewModel
import com.msoula.hobbymatchmaker.presentation.components.App
import com.msoula.hobbymatchmaker.presentation.components.LocalAppViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    //private val auth: FirebaseAuth by inject()
    //private lateinit var authStateListener: AuthStateListener
    private val appViewModel: AppViewModel by viewModel()

    private val googleUIClient = AndroidGoogleUIClient(CredentialManager.create(this), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HobbyMatchmakerTheme {
                CompositionLocalProvider(LocalAppViewModel provides appViewModel) {
                    Navigator(App(googleUIClient))
                }
            }
        }
    }

    /* override fun onStart() {
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
    } */
}
