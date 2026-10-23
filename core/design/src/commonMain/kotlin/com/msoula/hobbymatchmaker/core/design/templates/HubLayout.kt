package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

/**
 * Hub screen: recent matches take 1/3 of the height, favorite movies the remaining 2/3.
 * When there are no recent matches, both sections share the screen equally so the invitation
 * card has room to breathe.
 */
@Composable
fun HubLayout(
    modifier: Modifier = Modifier,
    isRecentMatchesEmpty: Boolean = false,
    hubRecentMatches: @Composable () -> Unit,
    hubFavoriteMovies: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = CustomSize.Sixteen)
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            hubRecentMatches()
        }
        SpacerHeight16()
        Box(modifier = Modifier.fillMaxWidth().weight(if (isRecentMatchesEmpty) 1f else 2f)) {
            hubFavoriteMovies()
        }
    }
}
