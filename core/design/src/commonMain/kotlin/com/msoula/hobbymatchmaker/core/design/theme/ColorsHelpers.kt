package com.msoula.hobbymatchmaker.core.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun disabledContainerColor(): Color {
    return if (isDarkModeEnabled())
        md_theme_dark_disabledContainer else
        md_theme_light_disabledContainer
}

@Composable
fun onDisabledColor(): Color {
    return if (isDarkModeEnabled())
        md_theme_dark_onDisabled else
        md_theme_light_onDisabled
}

@Composable
fun successContainerColor(): Color {
    return if (isDarkModeEnabled())
        md_theme_dark_successContainer else
        md_theme_light_successContainer
}

@Composable
fun HMMTextFieldColors(): TextFieldColors = TextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),

    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,

    cursorColor = MaterialTheme.colorScheme.primary,
    errorCursorColor = MaterialTheme.colorScheme.error,

    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
    disabledIndicatorColor = Color.Transparent,
    errorIndicatorColor = MaterialTheme.colorScheme.error,

    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),

    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
    errorLabelColor = MaterialTheme.colorScheme.error
)
