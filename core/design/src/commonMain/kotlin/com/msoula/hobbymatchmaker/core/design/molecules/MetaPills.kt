package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

@Composable
fun MovieMetaPill(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = .65f),
                shape = RoundedCornerShape(CustomSize.Sixteen)
            )
            .padding(horizontal = CustomSize.Eight, vertical = CustomSize.Eight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}
