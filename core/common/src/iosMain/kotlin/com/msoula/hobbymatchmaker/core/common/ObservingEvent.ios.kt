package com.msoula.hobbymatchmaker.core.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
actual fun <T> ObserveEvents(flow: Flow<T>, handler: @Composable ((T) -> Unit)) {
    var last: T? = null
    LaunchedEffect(flow) {
        flow.collect { last = it }
    }
    last?.let { handler(it) }
}
