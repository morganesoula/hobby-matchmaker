package com.msoula.hobbymatchmaker.core.common

@androidx.compose.runtime.Composable
actual fun <T> observeFlowWithLifecycle(
    flow: kotlinx.coroutines.flow.Flow<T>,
    onEvent: suspend (T) -> Unit
) {
}
