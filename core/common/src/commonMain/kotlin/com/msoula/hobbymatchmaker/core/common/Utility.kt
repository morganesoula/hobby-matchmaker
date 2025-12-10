package com.msoula.hobbymatchmaker.core.common

import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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

@OptIn(ExperimentalTime::class)
fun Instant.toTimeAgo(): String {
    val now = Clock.System.now()
    val difference = (now - this).inWholeSeconds
    return difference.toTimeAgo()
}

@OptIn(ExperimentalTime::class)
fun Long.toTimeAgo(): String {
    val now = Clock.System.now().epochSeconds
    val differenceInSeconds = now - this
    val locale = getDeviceLocale()
    val isFrench = locale.startsWith("fr", ignoreCase = true)

    val timeValue = when {
        differenceInSeconds < 60 -> return if (isFrench) "maintenant" else "now"
        differenceInSeconds < 120 -> "1m"
        differenceInSeconds < 3600 -> "${differenceInSeconds / 60}m"
        differenceInSeconds < 7200 -> "1h"
        differenceInSeconds < 86400 -> "${differenceInSeconds / 3600}h"
        differenceInSeconds < 172800 -> "1j"
        else -> "${differenceInSeconds / 86400}j"
    }

    return if (isFrench) "il y a $timeValue" else "$timeValue ago"
}

fun Double.formatOneDecimal(): String {
    val rounded = (this * 10).toInt() / 10.0

    return if (rounded % 1.0 == 0.0) {
        rounded.toInt().toString()
    } else rounded.toString()
}
