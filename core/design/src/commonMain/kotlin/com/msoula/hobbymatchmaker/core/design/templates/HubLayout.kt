package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16

@Composable
fun HubLayout(
    modifier: Modifier = Modifier,
    hubRecentMatches: @Composable () -> Unit,
    hubFavoriteMovies: @Composable () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        hubRecentMatches()
        SpacerHeight16()
        hubFavoriteMovies()
    }
}
