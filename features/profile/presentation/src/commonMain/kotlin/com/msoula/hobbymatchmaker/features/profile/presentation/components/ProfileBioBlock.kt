package com.msoula.hobbymatchmaker.features.profile.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import com.msoula.hobbymatchmaker.features.profile.presentation.models.ProfileMode
import com.msoula.hobbymatchmaker.features.profile.presentation.models.UserProfileUiEventModel

@Composable
fun BioBlock(
    mode: ProfileMode,
    bio: String?,
    onEvent: (UserProfileUiEventModel) -> Unit
) {
    if (mode == ProfileMode.View) {
        if (!bio.isNullOrBlank()) {
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        OutlinedTextField(
            value = bio!!,
            onValueChange = { onEvent(UserProfileUiEventModel.OnBioChanged(it)) },
            label = { Text("Short bio") },
            minLines = 3
        )
    }
}
