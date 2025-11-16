package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight32

@Composable
fun GuestProfileLayout(
    modifier: Modifier = Modifier,
    guestHeader: @Composable () -> Unit,
    guestDiscoverFeature: @Composable () -> Unit,
    guestBuildCircleFeature: @Composable () -> Unit,
    guestSharedInterestsFeature: @Composable () -> Unit,
    guestRedirectFeature: @Composable () -> Unit
) {
    guestHeader()
    SpacerHeight32()
    guestDiscoverFeature()
    SpacerHeight16()
    guestBuildCircleFeature()
    SpacerHeight16()
    guestSharedInterestsFeature()
    SpacerHeight16()
    guestRedirectFeature()
}
