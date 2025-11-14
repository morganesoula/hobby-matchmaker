package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

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

@Composable
fun MovieListLoadingScreen(
    itemCount: Int = 3
) {
    LazyColumn {
        items(itemCount) {
            ShimmerCard(height = 300.dp)
        }
    }
}

@Composable
fun MovieDetailLoadingScreen() {
    LoadingOverlay(true)
}

@Composable
fun ProfileLoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
    ) {
        LoadingCircularProgress()
        /* ShimmerCircle(modifier = modifier.size(96.dp).align(Alignment.CenterHorizontally))
        Spacer(modifier.height(12.dp))
        ShimmerRectangle(
            widthFraction = 0.5f,
            height = 20.dp,
            modifier = modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier.height(24.dp))
        ShimmerCard(height = 120.dp) */
    }
}
