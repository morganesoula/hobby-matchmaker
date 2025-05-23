package com.msoula.hobbymatchmaker.presentation

import androidx.compose.ui.window.ComposeUIViewController
import com.msoula.hobbymatchmaker.core.authentication.domain.models.ProviderType
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchmakerTheme
import com.msoula.hobbymatchmaker.core.login.presentation.clients.AppleUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClientImpl
import com.msoula.hobbymatchmaker.core.login.presentation.clients.IosAppleUIClient
import com.msoula.hobbymatchmaker.core.login.presentation.clients.IosFacebookUIClient
import com.msoula.hobbymatchmaker.presentation.clients.IosGoogleUIClient
import com.msoula.hobbymatchmaker.presentation.navigation.App
import com.msoula.hobbymatchmaker.presentation.navigation.getRootComponent

fun MainViewController() = ComposeUIViewController {
    val rootComponent = getRootComponent()

    val socialClients = mapOf(
        ProviderType.GOOGLE to GoogleUIClientImpl(IosGoogleUIClient()),
        ProviderType.APPLE to AppleUIClientImpl(IosAppleUIClient())
    )

    HobbyMatchmakerTheme {
        App(
            component = rootComponent,
            socialClients = socialClients,
            facebookUIClient = IosFacebookUIClient()
        )
    }
}
