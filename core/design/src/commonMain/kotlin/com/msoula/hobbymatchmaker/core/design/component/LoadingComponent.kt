package com.msoula.hobbymatchmaker.core.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics

@Composable
fun LoadingCircularProgress(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun LoadingOverlay(visible: Boolean) {
    if (!visible) return

    Box(
        Modifier
            .fillMaxSize()
            .semantics { hideFromAccessibility() }
    ) {
        Box(
            Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f))
        )
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}
