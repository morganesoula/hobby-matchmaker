package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.msoula.hobbymatchmaker.core.design.icons.BootstrapArrowRight
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

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
    }
}

@Composable
fun LoadMoreDataIndicator(
    isLoading: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(120.dp)
            .fillMaxHeight()
            .clickable(enabled = !isLoading) { onLoadMore() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(CustomSize.ThirtyTwo),
                strokeWidth = CustomSize.Two
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = BootstrapArrowRight,
                    contentDescription = "Load more",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SpacerHeight8()
                Text(
                    text = "Plus de films",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
