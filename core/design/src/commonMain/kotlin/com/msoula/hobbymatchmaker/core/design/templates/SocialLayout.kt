package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SocialLayout(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    header: @Composable () -> Unit,
    circleCard: (@Composable () -> Unit)? = null,
    tabRow: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.padding(paddingValues)) {
        header()
        circleCard?.invoke()
        tabRow()
        content()
    }
}
