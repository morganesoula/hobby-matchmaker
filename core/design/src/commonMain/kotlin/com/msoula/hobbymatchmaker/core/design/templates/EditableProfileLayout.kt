package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16

@Composable
fun EditableProfileLayout(
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
