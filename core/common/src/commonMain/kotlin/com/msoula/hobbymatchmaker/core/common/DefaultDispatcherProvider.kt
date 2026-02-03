package com.msoula.hobbymatchmaker.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob

class DefaultDispatcherProvider : DispatcherProvider {

    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val main: CoroutineDispatcher = Dispatchers.Main

    override fun createScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + io)
    }
}
