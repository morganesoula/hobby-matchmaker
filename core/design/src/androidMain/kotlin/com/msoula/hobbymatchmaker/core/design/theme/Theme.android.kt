package com.msoula.hobbymatchmaker.core.design.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun HobbyMatchmakerTheme(
    darkTheme: Boolean = isDarkModeEnabled(),
    content: @Composable () -> Unit
) {
    val colorScheme = getColorScheme(darkTheme)

    ApplyStatusBarColor(colorScheme, darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = getTypography(),
        content = content
    )
}

@Composable
actual fun getColorScheme(darkTheme: Boolean): ColorScheme {
    val context = LocalContext.current

    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (darkTheme) dynamicDarkColorScheme(
            context
        ) else dynamicLightColorScheme(context)

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
}

@Composable
actual fun isDarkModeEnabled(): Boolean = isSystemInDarkTheme()

@Composable
private fun ApplyStatusBarColor(colorScheme: ColorScheme, darkTheme: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }
}
