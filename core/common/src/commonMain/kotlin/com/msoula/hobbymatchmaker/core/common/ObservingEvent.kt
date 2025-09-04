package com.msoula.hobbymatchmaker.core.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> ObserveEvents(
    flow: Flow<T>,
    handler: @Composable (T) -> Unit
) {
    var lastEvent by remember { mutableStateOf<T?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(flow, lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { lastEvent = it }
        }
    }

    lastEvent?.let { handler(it) }
}

@Composable
fun SnackEffect(host: SnackbarHostState, uiText: UIText, key: Any) {
    val message = uiText.asString()
    LaunchedEffect(key) { host.showSnackbar(message) }
}

@Composable
fun CallOnceEffect(key: Any, block: suspend () -> Unit) {
    LaunchedEffect(key) { block() }
}
