package com.msoula.hobbymatchmaker.core.network

import platform.Foundation.NSBundle

actual class Utility {
    actual companion object {
        actual fun getPlatformTMDBKey(): String {
            val value = NSBundle.mainBundle.objectForInfoDictionaryKey("TMDB_KEY") as? String
            require(!value.isNullOrBlank()) { "TMDB key not found" }
            return value
        }
    }
}
