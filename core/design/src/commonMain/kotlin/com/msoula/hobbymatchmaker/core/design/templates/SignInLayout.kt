package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerHeight16
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWeight1
import com.msoula.hobbymatchmaker.core.design.atoms.keyboardDismissOnTap

@Composable
fun SignInLayout(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    topBar: @Composable () -> Unit,
    form: @Composable () -> Unit,
    divider: @Composable () -> Unit,
    socialMediaSection: @Composable () -> Unit,
    bottomSection: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .keyboardDismissOnTap()
    ) {
        topBar()
        SpacerWeight1()
        form()
        SpacerHeight16()
        divider()
        SpacerHeight16()
        socialMediaSection()
        SpacerWeight1()
        bottomSection()
    }
}
