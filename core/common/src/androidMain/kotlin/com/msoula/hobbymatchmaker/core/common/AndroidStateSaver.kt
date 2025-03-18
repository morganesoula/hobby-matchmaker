package com.msoula.hobbymatchmaker.core.common

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow

class AndroidStateSaver(
    private val savedStateHandle: SavedStateHandle
) : StateSaver {

    override fun <T> getState(key: String, defaultValue: T): T {
        return savedStateHandle.get<T>(key) ?: defaultValue
    }

    override fun <T> updateState(key: String, update: (T) -> T) {
        Logger.d("Updating state in AndroidStateSaver with key: $key and update $update")
        val oldValue = savedStateHandle.get<T>(key)
        val newValue = oldValue?.let(update) ?: return
        savedStateHandle[key] = newValue
    }

    override fun clearAll() {
        for (key in savedStateHandle.keys()) {
            savedStateHandle.remove<Any>(key)
        }
    }

    override fun removeState(key: String) {
        savedStateHandle.remove<Any?>(key)
    }


    override fun <T> getStateFlow(key: String, defaultValue: T): MutableStateFlow<T> {
        val flow = MutableStateFlow(getState(key, defaultValue))
        return flow.also { stateFlow ->
            updateState<T>(key) { newValue ->
                stateFlow.value = newValue
                newValue
            }
        }
    }
}
