package com.msoula.hobbymatchmaker.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
actual fun <T> ObserveEvents(flow: Flow<T>, handler: @Composable ((T) -> Unit)) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var last: T? = null

    LaunchedEffect(flow, lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { last = it }
        }
    }

    last?.let { handler(it) }
}
