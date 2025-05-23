package com.msoula.hobbymatchmaker.core.common

expect fun logToPlatform(message: String, tag: String, level: LogLevel)

enum class LogLevel {DEBUG, INFO, WARNING, ERROR}

object Logger {
    private var formatter: LogFormatter? = null

    fun init(formatter: LogFormatter) {
        this.formatter = formatter
    }

    private fun formatMessage(message: String): String =
        formatter?.format(message) ?: message

    fun d(message: String, tag: String = "HMM DEBUG") =
        logToPlatform(formatMessage(message), tag, LogLevel.DEBUG)

    fun i(message: String, tag: String = "HMM INFO") =
        logToPlatform(formatMessage(message), tag, LogLevel.INFO)

    fun w(message: String, tag: String = "HMM WARNING") =
        logToPlatform(formatMessage(message), tag, LogLevel.WARNING)

    fun e(message: String, throwable: Throwable? = null, tag: String = "HMM ERROR") =
        logToPlatform(formatMessage(message), tag, LogLevel.ERROR)
}

interface LogFormatter {
    fun format(message: String): String
}
