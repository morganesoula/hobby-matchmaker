package com.msoula.hobbymatchmaker.app

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.compose.rememberNavController
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.GoogleAuthClient
import com.msoula.hobbymatchmaker.navigation.HobbyMatchMakerNavigationRoot
import com.msoula.hobbymatchmaker.presentation.AppViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("ComposeViewModelForwarding")
@Stable
@Composable
fun HobbyMatchMakerApp(
    googleAuthClient: GoogleAuthClient,
    appViewModel: AppViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    HobbyMatchMakerNavigationRoot(navController, appViewModel, googleAuthClient)
}
