package com.msoula.hobbymatchmaker.core.design.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun getColorScheme(darkTheme: Boolean): ColorScheme {
    return if (darkTheme) DarkColorScheme else LightColorScheme
}

@Composable
actual fun isDarkModeEnabled(): Boolean {
    return false
}
