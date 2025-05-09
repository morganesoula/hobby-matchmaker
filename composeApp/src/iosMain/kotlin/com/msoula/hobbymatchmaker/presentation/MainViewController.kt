package com.msoula.hobbymatchmaker.presentation

import androidx.compose.ui.window.ComposeUIViewController
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchmakerTheme
import com.msoula.hobbymatchmaker.core.login.presentation.clients.IosFacebookUIClient
import com.msoula.hobbymatchmaker.presentation.clients.IosGoogleUIClient
import com.msoula.hobbymatchmaker.presentation.navigation.App
import com.msoula.hobbymatchmaker.presentation.navigation.getRootComponent
import nativeIosPlayerShared.CustomVideoPlayerView

fun MainViewController() = ComposeUIViewController {
    val rootComponent = getRootComponent()

    HobbyMatchmakerTheme {
        App(
            component = rootComponent,
            googleUIClient = IosGoogleUIClient(),
            facebookUIClient = IosFacebookUIClient()
        )
    }
}
