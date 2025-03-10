package com.msoula.hobbymatchmaker.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.msoula.hobbymatchmaker.core.common.AuthUiStateModel
import com.msoula.hobbymatchmaker.presentation.AppViewModel

class App : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        //TODO Check if appViewModel survives...
        val appViewModel = koinScreenModel<AppViewModel>()
        val destinationCheck by appViewModel.authenticationState.collectAsState()

        when (destinationCheck) {
            is AuthUiStateModel.CheckingState -> navigator.push(SplashscreenDestination())
            is AuthUiStateModel.IsConnected -> navigator.push(MovieDestination())
            is AuthUiStateModel.NotConnected -> navigator.push(SignInDestination())
        }
    }
}

