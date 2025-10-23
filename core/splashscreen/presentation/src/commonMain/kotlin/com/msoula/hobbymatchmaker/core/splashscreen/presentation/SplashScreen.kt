package com.msoula.hobbymatchmaker.core.splashscreen.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.ic_launcher_foreground
import com.msoula.hobbymatchmaker.core.splashscreen.presentation.model.SplashUiState
import org.jetbrains.compose.resources.painterResource

@Composable
fun SplashScreenContent(
    modifier: Modifier = Modifier,
    state: SplashUiState,
    redirectToAuth: () -> Unit,
    redirectToMovies: () -> Unit
) {
    when (state) {
        is SplashUiState.Loading -> SplashLoading()
    }

}

@Composable
fun SplashError() {

}

@Composable
fun SplashLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(Res.drawable.ic_launcher_foreground),
            contentDescription = "SplashScreen logo"
        )
    }
}
