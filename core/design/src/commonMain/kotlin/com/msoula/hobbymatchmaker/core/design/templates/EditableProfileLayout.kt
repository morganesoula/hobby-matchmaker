package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import kotlin.math.log

@Composable
fun EditableProfileLayout(
    modifier: Modifier = Modifier,
    editPhotoProfileSection: @Composable () -> Unit,
    editBasicInformationSection: @Composable () -> Unit,
    editInterestsSection: @Composable () -> Unit,
    logOut: @Composable () -> Unit
) {
    editPhotoProfileSection()
    SpacerHeight16()
    editBasicInformationSection()
    SpacerHeight16()
    editInterestsSection()
    SpacerHeight16()
    logOut()
}
