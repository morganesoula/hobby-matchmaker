package com.msoula.hobbymatchmaker.core.login.presentation

import androidx.lifecycle.SavedStateHandle

class AndroidStateSaver(
    private val savedStateHandle: SavedStateHandle
) : StateSaver {

    override fun <T> getState(key: String, defaultValue: T): T {
        return savedStateHandle.get<T>(key) ?: defaultValue
    }

    override fun <T> updateState(key: String, update: (T) -> T) {
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
}
