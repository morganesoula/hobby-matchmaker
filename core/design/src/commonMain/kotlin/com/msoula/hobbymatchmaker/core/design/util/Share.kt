package com.msoula.hobbymatchmaker.core.design.util

import androidx.compose.runtime.Composable

/**
 * Returns a function that opens the system share sheet with the given plain text.
 * Android: chooser intent. iOS: UIActivityViewController.
 */
@Composable
expect fun rememberShareText(): (text: String) -> Unit
