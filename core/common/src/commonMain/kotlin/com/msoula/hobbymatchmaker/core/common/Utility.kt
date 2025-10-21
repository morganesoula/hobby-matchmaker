package com.msoula.hobbymatchmaker.core.common

import kotlinx.datetime.LocalDate

expect fun getDeviceLocale(): String
expect fun isIosPlatform(): Boolean

fun String.extractYear(): String = runCatching {
    LocalDate.parse(this).year.toString()
}.getOrDefault("")

fun Int.toReadableDuration(): String {
    val hours = this / 60
    val minutes = this % 60

    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0) append("${minutes}m")
    }.trim()
}

fun Double.formatOneDecimal(): String {
    val rounded = (this * 10).toInt() / 10.0

    return if (rounded % 1.0 == 0.0) {
        rounded.toInt().toString()
    } else rounded.toString()
}
