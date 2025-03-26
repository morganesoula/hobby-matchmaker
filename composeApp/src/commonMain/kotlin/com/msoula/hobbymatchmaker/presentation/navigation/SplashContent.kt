package com.msoula.hobbymatchmaker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.SplashScreenContent
import kotlinx.coroutines.delay

@Composable
fun SplashContent(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onFinished()
    }

    SplashScreenContent()
}
