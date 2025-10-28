package com.msoula.hobbymatchmaker.features.profile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditBottomBar(
    canSave: Boolean,
    onSave: () -> Unit,
    onSkip: () -> Unit
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = { onSkip() }, modifier = Modifier.weight(1f)) {
                Text("Skip for now")
            }

            Button(onClick = { onSave() }, enabled = canSave, modifier = Modifier.weight(1f)) {
                Text("Save")
            }
        }
    }
}
