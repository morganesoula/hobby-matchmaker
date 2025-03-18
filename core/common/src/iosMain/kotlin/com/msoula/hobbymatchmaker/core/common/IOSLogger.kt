package com.msoula.hobbymatchmaker.core.common

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import platform.Foundation.NSLog

class IOSLogger: Antilog() {
    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        NSLog("[$tag] $message")
    }
}
