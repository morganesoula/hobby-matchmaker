package com.msoula.hobbymatchmaker.core.common

import kotlinx.coroutines.flow.MutableStateFlow

interface StateSaver {
    fun <T> getState(key: String, defaultValue: T): T
    fun <T> updateState(key: String, update: (T) -> T)
    fun clearAll()
    fun removeState(key: String)
    fun <T> getStateFlow(key: String, defaultValue: T): MutableStateFlow<T>
}
