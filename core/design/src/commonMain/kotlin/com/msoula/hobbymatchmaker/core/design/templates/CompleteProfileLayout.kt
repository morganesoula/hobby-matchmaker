package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16

@Composable
fun CompleteProfileLayout(
    headerSection: @Composable () -> Unit,
    favoritesSection: @Composable () -> Unit,
    interestsSection: @Composable () -> Unit
) {
    headerSection()
    SpacerHeight16()
    favoritesSection()
    SpacerHeight16()
    interestsSection()
}
