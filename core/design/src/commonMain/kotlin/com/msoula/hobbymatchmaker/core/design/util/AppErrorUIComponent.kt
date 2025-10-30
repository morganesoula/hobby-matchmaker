package com.msoula.hobbymatchmaker.core.design.util

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource

@Composable
fun UIText.asString(): String = when (this) {
    is UIText.Plain -> value
    is UIText.Resource -> stringResource(res, *args.toTypedArray())
}
