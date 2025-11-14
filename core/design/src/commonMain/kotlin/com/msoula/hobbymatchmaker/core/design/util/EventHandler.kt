package com.msoula.hobbymatchmaker.core.design.util

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class EventHandler {
    private val _events: Channel<UiEvent> = Channel(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun sendEvent(event: UiEvent) {
        if (!_events.isClosedForSend) {
            _events.send(event)
        }
    }

    fun close() {
        _events.close()
    }
}
