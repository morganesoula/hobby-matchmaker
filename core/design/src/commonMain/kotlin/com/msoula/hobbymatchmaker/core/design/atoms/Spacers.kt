package com.msoula.hobbymatchmaker.core.design.atoms

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

@Composable
fun SpacerHeight4(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(CustomSize.Four))
}

@Composable
fun SpacerHeight8(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(CustomSize.Eight))
}

@Composable
fun SpacerHeight16(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(CustomSize.Sixteen))
}

@Composable
fun SpacerWidth8(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.width(CustomSize.Eight))
}

@Composable
fun ColumnScope.SpacerWeight1(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.weight(1f))
}

@Composable
fun ColumnScope.SpacerWeight2(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.weight(2f))
}


