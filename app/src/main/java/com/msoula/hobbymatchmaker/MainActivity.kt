package com.msoula.hobbymatchmaker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.msoula.hobbymatchmaker.app.HobbyMatchMakerApp
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchmakerTheme
import com.msoula.hobbymatchmaker.core.navigation.Navigator
import com.msoula.hobbymatchmaker.core.session.domain.models.ConnexionMode
import com.msoula.hobbymatchmaker.core.session.domain.models.UserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.ClearUserUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SaveUserUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var saveUserUseCase: SaveUserUseCase

    @Inject
    lateinit var clearUserUseCase: ClearUserUseCase

    private lateinit var authStateListener: AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAuthenticationListener()
        val googleSignInClient = initGoogleAuth()

        setContent {
            HobbyMatchmakerTheme {
                HobbyMatchMakerApp(navigator, googleSignInClient)
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

    private fun initGoogleAuth(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.WEB_CLIENT_ID)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(this, gso)
    }

    private fun setAuthenticationListener() {
        authStateListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            user?.let { firebaseUser ->
                val email = firebaseUser.email

                val providers = firebaseUser.providerData.map { it.providerId }

                val connexionMode = when {
                    providers.contains("google.com") -> ConnexionMode.GOOGLE
                    providers.contains("facebook.com") -> ConnexionMode.FACEBOOK
                    else -> ConnexionMode.EMAIL
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    saveUserUseCase(UserDomainModel(email ?: "", connexionMode))
                }
            } ?: run {
                lifecycleScope.launch(Dispatchers.IO) {
                    clearUserUseCase()
                }

                Log.d("HMM", "User is not connected")
            }
        }
    }
}
