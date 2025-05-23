package com.msoula.hobbymatchmaker.core.common

import android.util.Log

actual fun logToPlatform(
    message: String,
    tag: String,
    level: LogLevel
) {
    when (level) {
        LogLevel.DEBUG -> Log.d(tag, message)
        LogLevel.INFO -> Log.i(tag, message)
        LogLevel.WARNING -> Log.w(tag, message)
        LogLevel.ERROR -> Log.e(tag, message)
    }
}
