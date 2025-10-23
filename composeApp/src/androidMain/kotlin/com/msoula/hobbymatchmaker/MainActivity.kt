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
import com.msoula.hobbymatchmaker.core.session.domain.useCases.ObserveIsConnectedUseCase
import com.msoula.hobbymatchmaker.presentation.navigation.App
import com.msoula.hobbymatchmaker.presentation.navigation.getRootComponent
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    private val callbackManager = CallbackManager.Factory.create()
    private val observeIsConnectedUseCase = getKoin().get<ObserveIsConnectedUseCase>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val googleUIClient = AndroidGoogleUIClient(CredentialManager.create(this), this)
        val facebookUIClient = AndroidFacebookUIClient({ this }, callbackManager)

        val socialClients: Map<ProviderType, SocialUIClient> = mapOf(
            ProviderType.GOOGLE to GoogleUIClientImpl(googleUIClient),
            ProviderType.FACEBOOK to FacebookUIClientImpl(facebookUIClient)
        )

        setContent {
            val rootComponent = getRootComponent()

            App(
                component = rootComponent,
                socialClients = socialClients,
                facebookUIClient = facebookUIClient,
                isConnected = true,
                onFinishApp = { finish() }
            )
        }
    }

    @Deprecated("")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
