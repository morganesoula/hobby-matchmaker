package com.msoula.hobbymatchmaker.core.network

actual class Utility {
    actual companion object {
        actual fun getPlatformTMDBKey() = BuildConfig.TMDB_KEY
    }
}
