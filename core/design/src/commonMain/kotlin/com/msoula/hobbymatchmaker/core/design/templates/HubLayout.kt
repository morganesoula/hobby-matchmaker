package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight32

@Composable
fun HubLayout(
    modifier: Modifier = Modifier,
    hubRecentMatches: @Composable () -> Unit,
    hubFavoriteMovies: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        hubRecentMatches()
        SpacerHeight32()
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            hubFavoriteMovies()
        }
    }
}
