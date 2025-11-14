package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight8

@Composable
fun CompleteProfileLayout(
    headerSection: @Composable () -> Unit,
    statsSection: @Composable () -> Unit,
    interestsSection: @Composable () -> Unit,
    socialSection: @Composable () -> Unit
) {
    headerSection()
    SpacerHeight8()
    statsSection()
    SpacerHeight16()
    interestsSection()
    SpacerHeight16()
    socialSection()
}
