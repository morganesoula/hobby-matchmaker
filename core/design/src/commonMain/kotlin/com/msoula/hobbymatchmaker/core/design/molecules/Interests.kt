package com.msoula.hobbymatchmaker.core.design.molecules

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.msoula.hobbymatchmaker.core.design.theme.CustomSize

@Composable
fun InterestsBlock(
    modifier: Modifier = Modifier,
    interests: List<String>?
) {
    FlowRow {
        interests?.forEach { tag ->
            AssistChip(
                modifier = modifier.padding(CustomSize.Four),
                onClick = {},
                label = { Text(tag) },
                enabled = false
            )
        }
    }
}
