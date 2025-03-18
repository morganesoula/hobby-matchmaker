package com.msoula.hobbymatchmaker.core.common

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel
import timber.log.Timber

class AndroidLogger : Antilog() {
    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        Timber.tag(tag ?: "Napier").log(priority.ordinal, message, throwable)
    }
}
