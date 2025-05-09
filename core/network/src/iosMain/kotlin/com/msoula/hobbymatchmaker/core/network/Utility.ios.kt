package com.msoula.hobbymatchmaker.core.network

import platform.Foundation.NSBundle

actual class Utility {
    actual companion object {
        actual fun getPlatformTMDBKey(): String {
            val value = NSBundle.mainBundle.objectForInfoDictionaryKey("tmdb_key") as? String
            require(!value.isNullOrBlank()) { "TMDB key not found" }
            return value
        }
    }
}
