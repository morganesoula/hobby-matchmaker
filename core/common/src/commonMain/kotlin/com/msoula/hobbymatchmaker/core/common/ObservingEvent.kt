package com.msoula.hobbymatchmaker.core.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
expect fun <T> ObserveEvents(
    flow: Flow<T>,
    handler: @Composable (T) -> Unit
)

@Composable
fun SnackEffect(host: SnackbarHostState, uiText: UIText, key: Any) {
    val message = uiText.asString()
    LaunchedEffect(key) { host.showSnackbar(message) }
}

@Composable
fun CallOnceEffect(key: Any, block: suspend () -> Unit) {
    LaunchedEffect(key) { block() }
}
