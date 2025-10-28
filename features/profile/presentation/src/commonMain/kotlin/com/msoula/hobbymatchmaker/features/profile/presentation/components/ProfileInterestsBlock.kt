package com.msoula.hobbymatchmaker.features.profile.presentation.components

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.msoula.hobbymatchmaker.features.profile.presentation.models.ProfileMode

@Composable
fun InterestsBlock(
    mode: ProfileMode,
    interests: List<String>?
) {
    FlowRow {
        interests?.forEach { tag ->
            if (mode == ProfileMode.View) {
                AssistChip(onClick = {}, label = { Text(tag) }, enabled = false)
            }
        }
    }
}
