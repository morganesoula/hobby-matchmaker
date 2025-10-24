package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchMakerTheme
import com.msoula.hobbymatchmaker.core.login.presentation.clients.FacebookUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.signIn.SocialUIClient
import presentation.AppNavHost

@Composable
fun App(
    socialClients: Map<ProviderType, SocialUIClient>,
    facebookUIClient: FacebookUIClient
) {
    HobbyMatchMakerTheme {
        AppNavHost(
            modifier = Modifier.fillMaxSize(),
            facebookUIClient = facebookUIClient,
            socialClients = socialClients
        )
    }
}
