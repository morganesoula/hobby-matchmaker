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

class AndroidLogFormatter : LogFormatter {
    override fun format(message: String): String {
        val element = Throwable().stackTrace.getOrNull(4)
        val prefix = if (element != null) {
            "[${element.className}.${element.methodName}:${element.lineNumber}]"
        } else {
            "[Unknown]"
        }

        return "$prefix $message"
    }
}
