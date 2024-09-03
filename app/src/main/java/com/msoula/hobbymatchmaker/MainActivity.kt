package com.msoula.hobbymatchmaker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.msoula.hobbymatchmaker.app.HobbyMatchMakerApp
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchmakerTheme
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.GoogleAuthClient
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionConnexionModeDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.models.SessionUserDomainModel
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.ClearSessionDataUseCase
import com.msoula.hobbymatchmaker.core.session.domain.use_cases.SaveUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    val auth: FirebaseAuth by inject()
    val saveUserUseCase: SaveUserUseCase by inject()
    val clearDataUseCase: ClearSessionDataUseCase by inject()

    private lateinit var authStateListener: AuthStateListener

    private val googleAuthClient = GoogleAuthClient(CredentialManager.create(this), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAuthenticationListener()

        setContent {
            HobbyMatchmakerTheme {
                HobbyMatchMakerApp(googleAuthClient)
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
        authStateListener = AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            user?.let { firebaseUser ->
                val email = firebaseUser.email

                val providers = firebaseUser.providerData.map { it.providerId }

                val connexionMode = when {
                    providers.contains("google.com") -> SessionConnexionModeDomainModel.GOOGLE
                    providers.contains("facebook.com") -> SessionConnexionModeDomainModel.FACEBOOK
                    else -> SessionConnexionModeDomainModel.EMAIL
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    saveUserUseCase(SessionUserDomainModel(email ?: "", connexionMode))
                }
            } ?: run {
                lifecycleScope.launch(Dispatchers.IO) {
                    clearDataUseCase()
                }

                Log.d("HMM", "User is not connected")
            }
        }
    }
}
