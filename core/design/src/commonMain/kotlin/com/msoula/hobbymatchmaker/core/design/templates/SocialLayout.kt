package com.msoula.hobbymatchmaker.core.design.templates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

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
        tabRow()
        circleCard?.let {
            Box(modifier = Modifier.padding(bottom = CustomSize.Sixteen)) { it() }
        }
        content()
    }
}
