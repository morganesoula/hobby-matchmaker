package com.msoula.hobbymatchmaker.core.login.presentation.extensions

import androidx.lifecycle.SavedStateHandle

fun <T> SavedStateHandle.updateStateHandle(
    key: String,
    update: (T) -> T,
) {
    val oldValue = this.get<T>(key)
    val newValue = oldValue?.let(update) ?: return
    this[key] = newValue
}

fun <T> SavedStateHandle.clearAll() {
    for (key in this.keys()) {
        this.remove<T>(key)
    }
}
