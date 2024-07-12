package com.msoula.hobbymatchmaker.app

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.msoula.hobbymatchmaker.core.login.presentation.sign_in.GoogleAuthClient
import com.msoula.hobbymatchmaker.navigation.HobbyMatchMakerNavigationRoot
import com.msoula.hobbymatchmaker.presentation.AppViewModel

@SuppressLint("ComposeViewModelForwarding")
@Stable
@Composable
fun HobbyMatchMakerApp(
    googleAuthClient: GoogleAuthClient,
    appViewModel: AppViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    HobbyMatchMakerNavigationRoot(navController, appViewModel, googleAuthClient)
}
