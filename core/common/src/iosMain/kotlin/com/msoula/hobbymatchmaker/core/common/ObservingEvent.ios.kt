package com.msoula.hobbymatchmaker.core.common

import androidx.compose.runtime.Composable
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Composable
actual fun <T> observeFlowWithLifecycle(
    flow: Flow<T>,
    onEvent: (T) -> Unit
) {
    GlobalScope.launch {
        flow.collect(onEvent)
    }
}
