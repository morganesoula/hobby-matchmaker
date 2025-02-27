package com.msoula.hobbymatchmaker.core.common

import platform.Foundation.NSUserDefaults

class IosStateSaver : StateSaver {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun <T> getState(key: String, defaultValue: T): T {
        return (defaults.objectForKey(key) as? T) ?: defaultValue
    }

    override fun <T> updateState(key: String, update: (T) -> T) {
        val oldValue = getState(key, defaultValue = null)
        val newValue = oldValue?.let(update) ?: return
        defaults.setObject(newValue, forKey = key)
    }

    override fun clearAll() {
        defaults.dictionaryRepresentation().keys.forEach { key ->
            defaults.removeObjectForKey(key.toString())
        }
    }

    override fun removeState(key: String) {
        defaults.removeObjectForKey(key)
    }
}
