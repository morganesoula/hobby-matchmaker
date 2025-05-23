package com.msoula.hobbymatchmaker.core.design.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import platform.UIKit.UIScreen
import platform.UIKit.UIUserInterfaceStyle

@Composable
fun HobbyMatchmakerTheme(
    darkTheme: Boolean = isDarkModeEnabled(),
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getTypography(),
        content = content
    )
}

@Composable
actual fun getColorScheme(darkTheme: Boolean): ColorScheme {
    return if (darkTheme) DarkColorScheme else LightColorScheme
}

@Composable
actual fun isDarkModeEnabled(): Boolean {
    return UIScreen.mainScreen.traitCollection.userInterfaceStyle == UIUserInterfaceStyle.UIUserInterfaceStyleDark
}
