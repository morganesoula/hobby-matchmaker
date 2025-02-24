package com.msoula.hobbymatchmaker.core.login.presentation

interface StateSaver {
    fun <T> getState(key: String, defaultValue: T): T
    fun <T> updateState(key: String, update: (T) -> T)
    fun clearAll()
    fun removeState(key: String)
}
