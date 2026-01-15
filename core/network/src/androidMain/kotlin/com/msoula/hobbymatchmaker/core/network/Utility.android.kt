package com.msoula.hobbymatchmaker.core.network

import com.msoula.hobbymatchmaker.core.network.BuildKonfig.TMDB_KEY


actual class Utility {
    actual companion object {
        actual fun getPlatformTMDBKey() = TMDB_KEY
    }
}
