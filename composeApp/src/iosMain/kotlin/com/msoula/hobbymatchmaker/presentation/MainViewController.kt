package com.msoula.hobbymatchmaker.presentation

import androidx.compose.ui.window.ComposeUIViewController
import com.msoula.hobbymatchmaker.core.login.presentation.clients.IosFacebookUIClient
import com.msoula.hobbymatchmaker.presentation.clients.IosGoogleUIClient
import com.msoula.hobbymatchmaker.presentation.navigation.App
import com.msoula.hobbymatchmaker.presentation.navigation.getRootComponent

fun MainViewController() = ComposeUIViewController {
    val rootComponent = getRootComponent()

    App(
        component = rootComponent,
        googleUIClient = IosGoogleUIClient(),
        facebookUIClient = IosFacebookUIClient()
    )
}
