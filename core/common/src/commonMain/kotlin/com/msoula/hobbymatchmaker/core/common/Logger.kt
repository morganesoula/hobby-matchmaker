package com.msoula.hobbymatchmaker.core.common

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

object Logger {
    fun init() {
        Napier.base(DebugAntilog())
    }

    fun d(message: String, tag: String = "HMM DEBUG") = Napier.d(message, tag = tag)
    fun i(message: String, tag: String = "HMM INFO") = Napier.i(message, tag = tag)
    fun w(message: String, tag: String = "HMM WARNING") = Napier.w(message, tag = tag)
    fun e(message: String, throwable: Throwable? = null, tag: String = "HMM ERROR") =
        Napier.e(message, throwable, tag = tag)
}
