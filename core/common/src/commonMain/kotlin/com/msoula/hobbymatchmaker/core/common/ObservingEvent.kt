package com.msoula.hobbymatchmaker.core.common

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

@Composable
expect fun <T> observeFlowWithLifecycle(
    flow: Flow<T>,
    onEvent: (T) -> Unit
)

@Composable
fun <T> ObserveAsEvents(flow: Flow<T>, onEvent: (T) -> Unit) {
    observeFlowWithLifecycle(flow, onEvent)
}
