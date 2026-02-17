package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight32

@Composable
fun HubLayout(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    hubRecentMatches: @Composable () -> Unit,
    hubFavoriteMovies: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        hubRecentMatches()
        SpacerHeight32()
        hubFavoriteMovies()
    }
}
