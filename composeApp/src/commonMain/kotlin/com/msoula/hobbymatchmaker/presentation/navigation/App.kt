package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.design.theme.HobbyMatchMakerTheme
import com.msoula.hobbymatchmaker.core.navigation.presentation.AppNavHost
import com.msoula.hobbymatchmaker.core.navigation.presentation.models.SocialClients

@Composable
fun App(
    socialClients: SocialClients
) {
    HobbyMatchMakerTheme {
        AppNavHost(
            socialClients = socialClients
        )
    }
}
