package com.msoula.hobbymatchmaker.app

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.msoula.hobbymatchmaker.core.di.navigation.Navigator
import com.msoula.hobbymatchmaker.navigation.HobbyMatchMakerNavHost
import com.msoula.hobbymatchmaker.presentation.AppViewModel

@SuppressLint("ComposeViewModelForwarding")
@Stable
@Composable
fun HobbyMatchMakerApp(navigator: Navigator, appViewModel: AppViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    DisposableEffect(key1 = navController) {
        navigator.setController(navController)
        onDispose {
            navigator.clear()
        }
    }

    HobbyMatchMakerNavHost(navController, navigator, appViewModel)
}
