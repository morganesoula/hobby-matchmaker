package com.msoula.hobbymatchmaker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.msoula.hobbymatchmaker.app.HobbyMatchMakerApp
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchmakerTheme
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.utils.GoogleAuthUIClient
import com.msoula.hobbymatchmaker.core.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var authStateListener: AuthStateListener

    private val googleAuthUiClient by lazy {
        GoogleAuthUIClient(
            this,
            FirebaseAuth.getInstance(),
            Identity.getSignInClient(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authStateListener = AuthStateListener {
            val user = it.currentUser

            Log.d("HMM", "Inside getAuthState with user: $user")
        }

        setContent {
            HobbyMatchmakerTheme {
                HobbyMatchMakerApp(navigator, googleAuthUiClient)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()

        authStateListener?.let {
            auth.removeAuthStateListener(it)
        }
    }
}
