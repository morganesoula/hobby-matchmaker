package com.msoula.hobbymatchmaker.core.common

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

object Logger {
    private var formatter: LogFormatter? = null

    fun init(formatter: LogFormatter) {
        this.formatter = formatter
        Napier.base(DebugAntilog())
    }

    private fun formatMessage(message: String): String =
        formatter?.format(message) ?: message

    fun d(message: String, tag: String = "HMM DEBUG") = Napier.d(formatMessage(message), tag = tag)
    fun i(message: String, tag: String = "HMM INFO") = Napier.i(formatMessage(message), tag = tag)
    fun w(message: String, tag: String = "HMM WARNING") =
        Napier.w(formatMessage(message), tag = tag)

    fun e(message: String, throwable: Throwable? = null, tag: String = "HMM ERROR") =
        Napier.e(formatMessage(throwable?.message ?: ""), throwable, tag = tag)
}

interface LogFormatter {
    fun format(message: String): String
}
