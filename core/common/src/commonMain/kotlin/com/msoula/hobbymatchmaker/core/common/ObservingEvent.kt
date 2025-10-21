package com.msoula.hobbymatchmaker.core.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> ObserveEvents(
    flow: Flow<T>,
    handler: @Composable (T) -> Unit
) {
    val latestHandler by rememberUpdatedState(handler)
    val event: T? by flow.collectAsState(initial = null)
    event?.let { latestHandler(it) }
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
