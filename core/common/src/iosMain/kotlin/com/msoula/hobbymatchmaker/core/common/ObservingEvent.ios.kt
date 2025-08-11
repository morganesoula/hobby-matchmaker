package com.msoula.hobbymatchmaker.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
actual fun <T> observeFlowWithLifecycle(
    flow: Flow<T>,
    onEvent: suspend (T) -> Unit
) {
    LaunchedEffect(flow) {
        flow.collect { onEvent(it) }
    }
}
