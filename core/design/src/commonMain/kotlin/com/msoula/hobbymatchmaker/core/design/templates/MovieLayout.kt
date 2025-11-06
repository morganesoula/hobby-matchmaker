package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun MovieLayout(
    snackBarHost: @Composable () -> Unit,
    topBar: @Composable () -> Unit,
    movieSection: @Composable (padding: PaddingValues) -> Unit
) {
    Scaffold(
        snackbarHost = snackBarHost,
        topBar = topBar
    ) { paddingValues ->
        movieSection(paddingValues)
    }
}
