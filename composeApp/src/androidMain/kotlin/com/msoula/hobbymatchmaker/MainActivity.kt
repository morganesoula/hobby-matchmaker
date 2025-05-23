package com.msoula.hobbymatchmaker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.credentials.CredentialManager
import com.facebook.CallbackManager
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchmakerTheme
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AndroidFacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AndroidGoogleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import com.msoula.hobbymatchmaker.presentation.navigation.App
import com.msoula.hobbymatchmaker.presentation.navigation.getRootComponent

class MainActivity : ComponentActivity() {
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val googleUIClient = AndroidGoogleUIClient(CredentialManager.create(this), this)
        val facebookUIClient = AndroidFacebookUIClient({ this }, callbackManager)

        val socialClients: Map<ProviderType, SocialUIClient> = mapOf(
            ProviderType.GOOGLE to GoogleUIClientImpl(googleUIClient),
            ProviderType.FACEBOOK to FacebookUIClientImpl(facebookUIClient)
        )

        setContent {
            val rootComponent = getRootComponent()

            HobbyMatchmakerTheme {
                App(
                    component = rootComponent,
                    socialClients = socialClients,
                    facebookUIClient = facebookUIClient
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
