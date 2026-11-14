package com.msoula.hobbymatchmaker.core.design.util

import androidx.compose.runtime.Composable

/**
 * Intercepts the system back action while [enabled].
 * Android: system back button/gesture. iOS: nothing to intercept, screens expose a back arrow instead.
 */
@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)
