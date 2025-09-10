package com.msoula.hobbymatchmaker.core.common

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

@Composable
actual fun <T> ObserveEvents(flow: Flow<T>, handler: @Composable ((T) -> Unit)) {}
