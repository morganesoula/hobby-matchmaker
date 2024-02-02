package com.msoula.hobbymatchmaker.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.compose.rememberNavController
import com.msoula.di.navigation.Navigator
import com.msoula.hobbymatchmaker.navigation.HobbyMatchMakerNavHost

@Composable
fun HobbyMatchMakerApp(navigator: Navigator) {
    val navController = rememberNavController()

    DisposableEffect(key1 = navController) {
        navigator.setController(navController)
        onDispose {
            navigator.clear()
        }
    }

    HobbyMatchMakerNavHost(navController, navigator)
}