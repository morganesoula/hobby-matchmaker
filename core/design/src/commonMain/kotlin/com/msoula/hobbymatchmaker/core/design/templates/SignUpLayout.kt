package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.atoms.SpacerWeight1
import com.msoula.hobbymatchmaker.core.design.atoms.keyboardDismissOnTap
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

@Composable
fun SignUpLayout(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    topBar: @Composable () -> Unit,
    form: @Composable () -> Unit,
    bottomSection: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(
                top = CustomSize.Sixteen,
                start = CustomSize.Sixteen,
                end = CustomSize.Sixteen
            )
            .navigationBarsPadding()
    ) {
        topBar()
        SpacerWeight1()
        form()
        SpacerWeight1()
        bottomSection()
    }
}
