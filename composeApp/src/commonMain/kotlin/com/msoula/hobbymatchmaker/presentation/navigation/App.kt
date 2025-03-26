package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClient
import com.msoula.hobbymatchmaker.core.navigation.domain.RootComponent

@Composable
fun App(component: RootComponent, modifier: Modifier = Modifier, googleUIClient: GoogleUIClient) {
    Children(stack = component.childSck, modifier = modifier) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Splash -> SplashContent(instance.onFinish)
            is RootComponent.Child.Auth -> AuthContent(instance.component, googleUIClient)
            is RootComponent.Child.Main -> MovieContent(instance.component)
            is RootComponent.Child.MovieDetail -> MovieDetailContent(instance.component)
        }
    }
}
