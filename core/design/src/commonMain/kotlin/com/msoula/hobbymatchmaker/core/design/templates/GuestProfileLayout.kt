package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight4

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
    guestDiscoverFeature()
    SpacerHeight4()
    guestBuildCircleFeature()
    SpacerHeight4()
    guestSharedInterestsFeature()
    SpacerHeight4()
    guestRedirectFeature()
}
