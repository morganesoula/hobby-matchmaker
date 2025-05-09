package com.msoula.hobbymatchmaker.core.common

import platform.Foundation.NSLog

actual fun logToPlatform(
    message: String,
    tag: String,
    level: LogLevel
) {
    val prefix = when (level) {
        LogLevel.DEBUG -> "🐛"
        LogLevel.INFO -> "ℹ️"
        LogLevel.WARNING -> "⚠️"
        LogLevel.ERROR -> "❌"
    }

    NSLog("$prefix [$tag] $message")
}
