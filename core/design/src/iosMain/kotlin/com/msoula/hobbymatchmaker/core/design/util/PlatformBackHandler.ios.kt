package com.msoula.hobbymatchmaker.core.design.util

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS has no system back button: the edit screen's back arrow already asks before discarding
}
