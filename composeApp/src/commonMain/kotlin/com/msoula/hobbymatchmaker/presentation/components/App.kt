package com.msoula.hobbymatchmaker.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.msoula.hobbymatchmaker.core.common.AuthUiStateModel
import com.msoula.hobbymatchmaker.core.login.presentation.clients.GoogleUIClient

class App(private val googleUIClient: GoogleUIClient) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val appViewModel = LocalAppViewModel.current
        val destinationCheck by appViewModel.authenticationState.collectAsState()

        LaunchedEffect(destinationCheck) {
            if (destinationCheck !is AuthUiStateModel.CheckingState) {
                navigator.replaceAll(
                    when (destinationCheck) {
                        is AuthUiStateModel.IsConnected -> MovieDestination()
                        is AuthUiStateModel.NotConnected -> SignInDestination(googleUIClient)
                        else -> SplashscreenDestination()
                    }
                )
            }
        }
    }
}

