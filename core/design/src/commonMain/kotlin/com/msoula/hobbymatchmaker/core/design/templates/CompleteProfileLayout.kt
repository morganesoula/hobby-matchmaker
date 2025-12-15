package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16

@Composable
fun CompleteProfileLayout(
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
