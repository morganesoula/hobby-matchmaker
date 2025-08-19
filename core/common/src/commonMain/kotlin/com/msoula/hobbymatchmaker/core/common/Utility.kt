package com.msoula.hobbymatchmaker.core.common

import kotlinx.datetime.LocalDate
import kotlin.coroutines.cancellation.CancellationException

expect fun getDeviceLocale(): String
expect fun isIosPlatform(): Boolean

suspend fun <Data, ErrorType : HMMAppError> safeCall(
    appError: (String) -> ErrorType,
    action: suspend () -> Data
): Result<Data, ErrorType> {
    return try {
        Result.Success(action())
    } catch (exception: CancellationException) {
        throw exception
    } catch (e: Exception) {
        Result.Failure(appError(e.message ?: "Unknown error"))
    }
}

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
