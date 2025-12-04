package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16

@Composable
fun CompleteProfileLayout(
    modifier: Modifier = Modifier,
    headerSection: @Composable () -> Unit,
    statsSection: @Composable () -> Unit,
    interestsSection: @Composable () -> Unit,
    socialSection: @Composable () -> Unit,
    logOut: @Composable () -> Unit
) {
    headerSection()
    SpacerHeight16()
    statsSection()
    SpacerHeight16()
    interestsSection()
    SpacerHeight16()
    socialSection()
    SpacerHeight16()
    logOut()
}
