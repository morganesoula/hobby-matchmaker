package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MovieDetailLayout(
    modifier: Modifier = Modifier,
    snackBarHost: @Composable () -> Unit,
    topBar: @Composable () -> Unit,
    movieDetailSection: @Composable (padding: PaddingValues) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        snackbarHost = snackBarHost,
        topBar = topBar
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            movieDetailSection(paddingValues)
        }
    }
}
