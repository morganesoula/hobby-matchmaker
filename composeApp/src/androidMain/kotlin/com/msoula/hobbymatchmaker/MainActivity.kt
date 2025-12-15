package com.msoula.hobbymatchmaker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.credentials.CredentialManager
import com.facebook.CallbackManager
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AndroidFacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AndroidGoogleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import com.msoula.hobbymatchmaker.core.navigation.presentation.models.SocialClients
import com.msoula.hobbymatchmaker.presentation.navigation.App

class MainActivity : ComponentActivity() {
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val googleUIClient = AndroidGoogleUIClient(CredentialManager.create(this), this)
        val facebookUIClient = AndroidFacebookUIClient(
            activityProvider = { this },
            callbackManager = callbackManager
        )

        val socialClients = SocialClients(
            clients = mapOf(
                ProviderType.GOOGLE to GoogleUIClientImpl(googleUIClient),
                ProviderType.FACEBOOK to FacebookUIClientImpl(facebookUIClient)
            )
        )

        setContent {
            App(
                socialClients = socialClients
            )
        }
    }

    @Deprecated("Required for Facebook SDK compatibility", level = DeprecationLevel.HIDDEN)
    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
